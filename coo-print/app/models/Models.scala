package models

case class LoginForm( userId : String, passwd : String )


case class OrderItem(
    fileName: String, 
    quantity: Int, 
    material: String, 
    color: String, 
    finish: String, 
    layer: String, 
    fill: String, 
    zoom: String, 
    price: Double,
    mxyz: String,
    mlayer: Double) {
  def ==(oi : OrderItem) = oi.fileName == this.fileName
}

object OrderItem {
  def apply(fileName: String) : OrderItem = OrderItem(fileName, 5, "PLA塑料", "原色", "无", "0.3", "15", "100", 10.15, "230 x 150 x 140 mm", 2)
  
  
}