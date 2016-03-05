package forms

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
    district: String = "宝山区",
    address: String = "详细地址",
    tel: String = "18812345678",
    email: String = "a@b.com",
    other: String = "qq",
    post: String = "other2",
    fc: Boolean = true
    )

