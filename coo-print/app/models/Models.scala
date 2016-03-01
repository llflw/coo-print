package models

case class LoginForm( userId : String, passwd : String )


case class OrderItem(fileName: String, quantity: Int, material: String, color: String, finish: String, layer: String, fill: String, zoom: String, xyz: String)

object OrderItem {
  def apply(fileName: String) : OrderItem = OrderItem(fileName, 1, "PLA塑料", "原色", "无", "0.3 mm", "15%", "原始尺寸", "230 x 150 x 140 mm")
}