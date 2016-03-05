package models

import forms._
import helpers.UserCache
import models._

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
