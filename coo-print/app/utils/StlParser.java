package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StlParser {
  
	private Path stlFilePath;
	
	private int numOfTriangles;
	private double volume;
	
	private float minx = Float.MAX_VALUE;
	private float maxx = Float.MIN_NORMAL;
	private float miny = Float.MAX_VALUE;
	private float maxy = Float.MIN_NORMAL;
	private float minz = Float.MAX_VALUE;
	private float maxz = Float.MIN_NORMAL;	
	
	private float xLen, yLen, zLen;
	
	private final static String regexFpn = "[-+]?\\b(?:[0-9]*\\.)?[0-9]+(?:[eE][-+]?[0-9]+)?\\b";
	private final static String regexFacet = 
			"facet\\s+normal\\s+(" + regexFpn + ")\\s+(" + regexFpn +")\\s+(" + regexFpn + ")\\s+"
			+ "outer\\s+loop\\s+"
			+ "vertex\\s+(" + regexFpn + ")\\s+(" + regexFpn +")\\s+(" + regexFpn + ")\\s+"
			+ "vertex\\s+(" + regexFpn + ")\\s+(" + regexFpn +")\\s+(" + regexFpn + ")\\s+"
			+ "vertex\\s+(" + regexFpn + ")\\s+(" + regexFpn +")\\s+(" + regexFpn + ")\\s+"
			+ "endloop\\s+"
			+ "endfacet";
	
	private final static Pattern p = Pattern.compile(regexFacet, Pattern.DOTALL);
	
	public StlParser(String filePath) {
		this.stlFilePath = Paths.get(filePath);		
	}
	public StlParser(Path filePath) {
		this.stlFilePath = filePath;		
	}
	
	public boolean parse() {
		try {
			byte[] data = Files.readAllBytes(stlFilePath);
			
			Vertex v1 = new Vertex();
			Vertex v2 = new Vertex();
			Vertex v3 = new Vertex();
			
			// try ASCII
			String lines = new String(data);
			
			Matcher m = p.matcher(lines);

			while (m.find()) {		
				v1.setXYZ(m.group(4), m.group(5), m.group(6));
				v2.setXYZ(m.group(7), m.group(8), m.group(9));
				v3.setXYZ(m.group(10), m.group(11), m.group(12));
				
				volume += signedVolumeOfTriangle(v1, v2, v3);
				seekBounds(v1, v2, v3);
				numOfTriangles++;
			}
			
			if (numOfTriangles == 0) {
				// Binary
				ByteBuffer bb = ByteBuffer.wrap(data, 80, data.length - 80);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				numOfTriangles = bb.getInt();
				while (bb.hasRemaining()) {
					bb.getFloat();
					bb.getFloat();
					bb.getFloat();
					v1.setXYZ(bb.getFloat(), bb.getFloat(), bb.getFloat());
					v2.setXYZ(bb.getFloat(), bb.getFloat(), bb.getFloat());
					v3.setXYZ(bb.getFloat(), bb.getFloat(), bb.getFloat());
					bb.getShort();
					
					volume += signedVolumeOfTriangle(v1, v2, v3);
					seekBounds(v1, v2, v3);
				}
			}
				
			this.xLen = this.maxx - this.minx;
			this.yLen = this.maxy - this.miny;
			this.zLen = this.maxz - this.minz;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private double signedVolumeOfTriangle(Vertex v1, Vertex v2, Vertex v3) {
				
		double v321 = v3.x * v2.y * v1.z;
		double v231 = v2.x * v3.y * v1.z;
		double v312 = v3.x * v1.y * v2.z;
		double v132 = v1.x * v3.y * v2.z;
		double v213 = v2.x * v1.y * v3.z;
		double v123 = v1.x * v2.y * v3.z;
		
		double vol = (1.0/6.0) * (-v321 + v231 + v312 - v132 - v213 + v123);
		
		return vol;
	}
	
	private void seekBounds(Vertex v1, Vertex v2, Vertex v3) {
		
		this.minx = Math.min(Math.min(v1.x, v2.x), Math.min(v3.x, this.minx));
		this.maxx = Math.max(Math.max(v1.x, v2.x), Math.max(v3.x, this.maxx));
		
		this.miny = Math.min(Math.min(v1.y, v2.y), Math.min(v3.y, this.miny));
		this.maxy = Math.max(Math.max(v1.y, v2.y), Math.max(v3.y, this.maxy));
		
		this.minz = Math.min(Math.min(v1.z, v2.z), Math.min(v3.z, this.minz));
		this.maxz = Math.max(Math.max(v1.z, v2.z), Math.max(v3.z, this.maxz));
	}
	
	static class Vertex {
		private float x; 
		private float y; 
		private float z; 
		public void setXYZ(String x, String y, String z) {
			this.x = Float.parseFloat(x);
			this.y = Float.parseFloat(y);
			this.z = Float.parseFloat(z);
		}
		public void setXYZ(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public Path getStlFilePath() {
		return stlFilePath;
	}

	public int getNumOfTriangles() {
		return numOfTriangles;
	}

	public double getVolume() {
		return volume;
	}
	
	public float getXLen() {
		return this.xLen;
	}
	public float getYLen() {
		return this.yLen;
	}
	public float getZLen() {
		return this.zLen;
	}
	
	public String getBox() {
		return getXLen() + " x " + getYLen() + " x " + getZLen();
	}
	
	
	
	public static void main(String[] args) {
		StlParser stl = new StlParser("/var/stl_test/b.STL");
		stl.parse();
		System.out.println("file = "+stl.getStlFilePath());
		System.out.println("volume = "+stl.getVolume()+" mm");
		System.out.println("numOfTriangles = "+stl.numOfTriangles);
		System.out.println("xyz = "+stl.getBox());
	}
}