package controllers

import scala.concurrent.Future
import javax.inject.Inject
import play.api._
import play.api.mvc._
import play.twirl.api._
import play.api.libs.json._
import play.api.cache._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import slick.driver.PostgresDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import play.api.i18n.Messages
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.libs.json._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.collection.mutable.ListBuffer
import utils.UserCache
import models._
import models.Tables._
import utils.DBUtils
import utils.StlParser

object Application {

  val SKeyObjectCount = "object_count"
  val SKeyOrderPrice = "order_price"
  val SKeyTotalPrice = "total_price"
  
  val FKeyShoppingNextStep = "shopping_next_step"
  
  val CKeyUploadFiles = "upload-files"
  val CKeyTableMMaterial = "table_m_material"
  val CKeyOrderContact= "order_contact"
  
  val PostMethodSF = Map("sf1" -> "顺丰标快","sf2" -> "顺丰特惠","sf3" -> "顺丰即日","sf4" -> "顺丰次晨")
  val PostMethodOther = Map("other1" -> "中通快递","other2" -> "圆通快递","other3" -> "申通快递")
  
}

class Application @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider, 
    val messagesApi: MessagesApi, 
    val cache: CacheApi
    ) extends Controller with I18nSupport {

  val userCache: UserCache = new UserCache(cache)
  
  
  val contactForm : Form[ContactForm] = Form(
        mapping(
          "addressee" -> nonEmptyText,
          "company" -> text,
          "province" -> nonEmptyText,
          "city" -> nonEmptyText,
          "district" -> nonEmptyText,
          "address" -> nonEmptyText,
          "tel" -> nonEmptyText,
          "email" -> nonEmptyText,
          "other" -> text,
          "post" -> nonEmptyText,
          "fc" -> boolean
          )(ContactForm.apply)(ContactForm.unapply)
        )
        
   def orderItemForm(fileName: String): Form[OrderItemForm] = {
    val fileNameShort = fileName.substring(0, fileName.indexOf("."))
    Form (
          single (
              fileNameShort -> mapping (                  
                "fileName" -> default(of[String], fileName),
                "quantity" -> number(min = 1, max = 8), 
                "material" -> number(min = 1, max = 7),
                "color" -> of[String], 
                "finish" -> of[String], 
                "layer" -> of[String],
                "fill" -> of[String], 
                "zoom" -> of[String]
              )(OrderItemForm.apply)(OrderItemForm.unapply)
          )
      )
  }
  
  object CPAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {

      import java.time.LocalDateTime
      Logger.info("Calling action:" + request.path + " - " + LocalDateTime.now())
      block(request).map { result => 
        if (request.session.get(UserCache.SKeyClientId).isEmpty) {          
          result.withSession(request.session + (UserCache.SKeyClientId -> UUID.randomUUID().toString()))
        } else result
      }
    }
  }

  
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import dbConfig.driver.api._
  
  /**
   * main page action
   */
  def index = CPAction { implicit request =>
    Ok( views.html.index())
  }
  
  
  def editItems = CPAction { implicit request =>
    
   val cachedMaterials = userCache.get[List[Tables.MMaterialRow]](Application.CKeyTableMMaterial).getOrElse{
      val materialRows : Future[Seq[Tables.MMaterialRow]] = dbConfig.db.run(MMaterial.result)
      val materials = Await.result(materialRows, Duration(1, "sec")).toList
      userCache.set[List[Tables.MMaterialRow]](Application.CKeyTableMMaterial, materials).get
    }     
    
    val orderItems = userCache.get[ListBuffer[OrderItem]](Application.CKeyUploadFiles).getOrElse(ListBuffer())
    val orderPrice = "%.2f".format(orderItems.foldLeft(0.0)(_ + _.price))
    Ok(views.html.editItems(orderItems.toList, cachedMaterials)).addingToSession(
        (Application.SKeyObjectCount -> orderItems.length.toString),
        (Application.SKeyOrderPrice -> orderPrice),
        (Application.SKeyTotalPrice -> orderPrice))
  }
  
  def getItem(fileName : String) = CPAction { implicit request =>
   
    val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKeyUploadFiles).getOrElse(ListBuffer())
    val cachedMaterials = userCache.get[List[Tables.MMaterialRow]](Application.CKeyTableMMaterial).getOrElse(List())

    val orderItems = allOrderItems.filter { oi => oi.fileName == fileName }
    if (orderItems.length > 0) {
      Ok(views.html.orderItem(orderItems(0), cachedMaterials))
    } else {
      Ok("")
    }
    
    
  }
  
  /**
   * login action
   */
  def login = CPAction.async { implicit request =>
    val loginUserForm = Form(
        mapping(
            "user_id" -> nonEmptyText,
            "passwd" -> nonEmptyText
        )(LoginForm.apply)(LoginForm.unapply)
    )
    val loginUser = loginUserForm.bindFromRequest().get
    
    val userRows: Future[Seq[PUserRow]] = dbConfig.db.run(PUser.filter{puser =>
      puser.userId === loginUser.userId && puser.passwd === loginUser.passwd}.result)
    
    userRows.map { users => 
      if (users.length > 0 ) {
        val userName = users(0).userName.get
        Ok(Json.obj(
          "status" -> "ok",
          "msg" -> JsString("hi, " + userName)
        )).addingToSession(("user_name" -> userName));
      } else {
        Ok(Json.obj(
          "status" -> "ng",
          "msg" -> Messages("login.auth.err")
        ));
      }
    }
  }
  
  /**
   * logout action
   */
  def logout = CPAction { implicit request =>
    userCache.remove();
    Redirect(routes.Application.index).withNewSession
  }
  
  /**
   * file upload action
   */
  def upload = CPAction(parse.multipartFormData) {implicit request =>
  
    request.body.file("files[]").map { file =>

      import java.io.File
      import java.nio.file.Path
      import java.nio.file.Files
      import java.nio.file.Paths
      
      val uploadFileDir = Play.current.configuration.getString("cp.upload.dir").getOrElse("/tmp")
      
      val cid = request.session.get(UserCache.SKeyClientId).get
      val pdir = Paths.get(uploadFileDir, cid)
      
      if (!Files.isDirectory(pdir)) {
        Files.createDirectory(pdir);
      }
      var filename = file.filename
      if (filename.indexOf(".") < 0) filename += ".stl"
      //val contentType = file.contentType

      
      val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKeyUploadFiles)
                          .getOrElse(userCache.set[ListBuffer[OrderItem]](Application.CKeyUploadFiles, ListBuffer.empty).get)
                      
      if (allOrderItems.filter { x => x.fileName == filename }.length > 0 ) {
        val idx = filename.indexOf(".")
        filename = filename.substring(0, idx) + "-" + allOrderItems.length.toString() + filename.substring(idx, filename.length)
      }
      val destFile = pdir.resolve(filename)      
      file.ref.moveTo(destFile.toFile())   
      
      val orderItem = OrderItem(filename)
      // stl file parser
      val stlParser = new StlParser(destFile)
      if (stlParser.parse()) {
        orderItem.volume = stlParser.getVolume;
        orderItem.xLen = stlParser.getXLen;
        orderItem.yLen = stlParser.getYLen;
        orderItem.zLen = stlParser.getZLen;
      }
      allOrderItems.append(orderItem)
      
      val json = Json.parse(s"""
        { "files":[{"name":"$filename"}] }
        """)
      val orderPrice = "%.2f".format(allOrderItems.foldLeft(0.0)(_ + _.price))
      val objCnt = allOrderItems.foldLeft(0)(_ + _.quantity)
      Ok(json).addingToSession(
          (Application.SKeyObjectCount -> objCnt.toString),
          (Application.SKeyOrderPrice -> orderPrice),
          (Application.SKeyTotalPrice -> orderPrice))
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
    
  }
  
  /**
   * get various material properties
   */
  def props(mid : String) = Action { implicit request =>
    val defaultJson = Json.parse("""{
         "color" : [""], "finish" : [""],"fill" : [""],"layer" : [""],"zoom" : [""]
          }""")
          
    userCache.get[List[Tables.MMaterialRow]](Application.CKeyTableMMaterial) match {
      case Some(materials) => 
        val filteredMrs = materials.filter { mr => mr.materialId == mid.toInt }
        if (filteredMrs.length > 0) {
         val fmr = filteredMrs(0)
         val json = Json.obj(
         "color" -> fmr.vColor.getOrElse("").split(","),  
         "finish" -> fmr.vFinish.getOrElse("").split(","),
         "fill" -> fmr.vFill.getOrElse("").split(","),
         "layer" -> fmr.vLayer.getOrElse("").split(","),
         "zoom" -> fmr.vZoom.getOrElse("").split(",")
          )
          Ok(json) 
        } else {
          Ok(defaultJson)
        }
      case None => Ok(defaultJson)
    }
  }
  
  /**
   * delete order item : uploaded file
   */
  def delItem(fileName : String) = Action { implicit request =>

    val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKeyUploadFiles).getOrElse(ListBuffer.empty)-=(OrderItem(fileName))
    
    val orderPrice = "%.2f".format(allOrderItems.foldLeft(0.0)(_ + _.price))
    val objCnt = allOrderItems.foldLeft(0)(_ + _.quantity)
    Ok("").addingToSession(
        (Application.SKeyObjectCount -> objCnt.toString),
        (Application.SKeyOrderPrice -> orderPrice),
        (Application.SKeyTotalPrice -> orderPrice))
  }
  
  /**
   * submit items
   */
  def submitItems = CPAction { implicit request =>
    val fileNameForm = Form(
      single(
        "fileNames" -> list(text)
      )
    )
    val fileNameList = fileNameForm.bindFromRequest().get
    
    val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKeyUploadFiles).getOrElse(ListBuffer.empty)
    val materials = userCache.get[List[Tables.MMaterialRow]](Application.CKeyTableMMaterial).getOrElse(List.empty)
    
    assert(fileNameList.length == allOrderItems.length)
    
    for(fileName <- fileNameList) {
      
      Logger.info(fileName)
      
      val cOrderItem = orderItemForm(fileName).bindFromRequest().get
      
      val orderItemList = allOrderItems.filter { x => x.fileName == cOrderItem.fileName }
      val material = materials.filter { x => x.materialId == cOrderItem.material }
      if (orderItemList.length > 0) {
        orderItemList(0).copyFrom(cOrderItem, material(0))        
      }
              
    }
    
    val orderPrice = "%.2f".format(allOrderItems.foldLeft(0.0)(_ + _.price))
    val objCnt = allOrderItems.foldLeft(0)(_ + _.quantity)

    Redirect(routes.Application.editContact).addingToSession(
        (Application.SKeyObjectCount -> objCnt.toString),
        (Application.SKeyOrderPrice -> orderPrice),
        (Application.SKeyTotalPrice -> orderPrice));
  }
  
  
  /**
   * edit contact
   */
  def editContact = CPAction { implicit request =>
    
    val contact : ContactForm = userCache.get[ContactForm](Application.CKeyOrderContact).getOrElse(ContactForm())    
    Ok(views.html.editContact(contactForm.fill(contact)))
  }
  
  /**
   * submit contact
   */
  def submitContact = CPAction { implicit request =>
  
    contactForm.bindFromRequest().fold(
      formWithErrors => {
        Logger.error("contact validate error")
        BadRequest(views.html.editContact(formWithErrors))
      },
      userData => {           
        userCache.set(Application.CKeyOrderContact, userData)    
        Redirect(routes.Application.confirmOrder)
      }
    )
  }
  
  def confirmOrder = CPAction { implicit request =>
    val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKeyUploadFiles).getOrElse(ListBuffer.empty)
    val contact : ContactForm = userCache.get[ContactForm](Application.CKeyOrderContact).getOrElse(ContactForm())
    
    Ok(views.html.confirmOrder(allOrderItems.toList, contact));
  }
  
  
  def home = CPAction{ implicit request =>
    
    Ok(views.html.home())
  }  
}
