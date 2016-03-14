package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import play.mvc.Controller;
import play.mvc.Result;

public class JApplication extends Controller {

	public Result obj(String fileName) {
		try {
		Path  p = Paths.get("/var/obj_3d",fileName);
	
		if(fileName.endsWith(".jpg") || fileName.endsWith(".stl")) {
			
			return ok(Files.readAllBytes(p));
		} else {
			String str = "";
			for (String line : Files.readAllLines(p)) {
				str += line + "\n";
			}
			
			Result r = ok(str);
			return r;
		}
		
		}catch (Exception e ) {
			return ok();
		}
		
	}
	
	public Result objInfo(String fileName) {
		
		Path  p = Paths.get("/var/obj_3d",fileName);
		long size = 0;
		try {
			size = Files.size(p);
		} catch (IOException e ) {
			
		}
		response().setHeader("Content-Length", String.valueOf(size));
		
		return ok();

		
	}
	
	
}
