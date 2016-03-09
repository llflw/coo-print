package models

import utils.UserCache

case class LoginForm( userId : String, passwd : String )

case class OrderItemForm(
    fileName: String = "",
    quantity: Int = 1, 
    material: Int = 1, 
    color: String = "", 
    finish: String = "", 
    layer: String = "", 
    fill: String = "", 
    zoom: String = ""
    )
    
case class ContactForm(
    addressee: String = "某某某",
    company: String = "XXXX有限公司",
    province: String = "上海",
    city: String = "上海市",
    district: String = "浦东新区",
    address: String = "详细地址",
    tel: String = "12312345678",
    email: String = "a@b.com",
    other: String = "qq",
    post: String = "other2",
    fc: Boolean = true
    )

    
case class OrderItem(
    val fileName: String, 
    var quantity: Int, 
    var material: Int, 
    var materialName: String,
    var color: String, 
    var finish: String, 
    var layer: String, 
    var fill: String, 
    var zoom: String,
    var materialPrice: Double,
    var price: Double,
    val mxyz: String,
    val mlayer: Double) {
  def ==(oi : OrderItem) = oi.fileName == this.fileName
  
  def copyFrom(orderItemForm : OrderItemForm, material: Tables.MMaterialRow) : OrderItem = {
    this.quantity = orderItemForm.quantity
    this.material = orderItemForm.material
    this.color = orderItemForm.color
    this.finish = orderItemForm.finish
    this.layer = orderItemForm.layer
    this.fill = orderItemForm.fill
    this.zoom = orderItemForm.zoom
    
    
    this.materialName = material.materialName
    this.materialPrice = material.price.doubleValue()
    this.price = this.quantity * this.materialPrice
    
    this
  }
  
}

object OrderItem {
  def apply(fileName: String) : OrderItem = OrderItem(fileName, 1, 1, "PLA塑料", "原色", "无", "0.3", "15", "100", 100.1, 100.1, "230 x 150 x 140 mm", 2)
}
