package controllers


import scala.concurrent.Future
import javax.inject.Inject
import play.api._
import play.api.mvc._
import play.twirl.api._
import play.api.libs.json._
import play.api.cache._
import play.api.data._
import play.api.data.Forms._
import slick.driver.PostgresDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import models._
import models.Tables._
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

import helpers._

object Application {

  val SKEY_OF_UPLOAD_FILE_COUNT = "uploadFileCount"
  val SKEY_OF_ORDER_PRICE = "orderPrice"
  val SKEY_OF_TOTAL_PRICE = "totalPrice"
  
  val FKEY_OF_SHOPPING_NEXT_STEP = "shoppingNextStep"
  
  val CKEY_OF_UPLOAD_FILES = "uploadFiles"
  val CKEY_OF_TABLE_MMATERIAL = "table_m_material"
}

class Application @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val messagesApi: MessagesApi, val cache: CacheApi) extends Controller with I18nSupport {

  val userCache: UserCache = new UserCache(cache)
  
  object CPAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      import java.time.LocalDateTime
      Logger.info("Calling action:" + request.path + " - " + LocalDateTime.now())
      block(request).map { result => 
        if (request.session.get(UserCache.SKEY_OF_CLIENT_ID).isEmpty) {          
          result.withSession(request.session + (UserCache.SKEY_OF_CLIENT_ID -> UUID.randomUUID().toString()))
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
    
   val cachedMaterials = userCache.get[List[Tables.MMaterialRow]](Application.CKEY_OF_TABLE_MMATERIAL).getOrElse{
      val materialRows : Future[Seq[Tables.MMaterialRow]] = dbConfig.db.run(MMaterial.result)
      val materials = Await.result(materialRows, Duration(1, "sec")).toList
      userCache.set[List[Tables.MMaterialRow]](Application.CKEY_OF_TABLE_MMATERIAL, materials).get
    }     
    
    val orderItemList = userCache.get[ListBuffer[OrderItem]](Application.CKEY_OF_UPLOAD_FILES).getOrElse(ListBuffer())
    
    Ok(views.html.editItems(orderItemList.toList, cachedMaterials))
  }
  
  def getItem(fileName : String) = CPAction { implicit request =>
   
    val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKEY_OF_UPLOAD_FILES).getOrElse(ListBuffer())
    val cachedMaterials = userCache.get[List[Tables.MMaterialRow]](Application.CKEY_OF_TABLE_MMATERIAL).getOrElse(List())

    val orderItems = allOrderItems.filter { oi => oi.fileName == fileName }
    if (orderItems.length > 0) {
      Ok(views.html.orderItem(allOrderItems.length, orderItems(0), cachedMaterials))
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
      
      val c_id = request.session.get(UserCache.SKEY_OF_CLIENT_ID).get
      val pdir = Paths.get(uploadFileDir, c_id)
      
      if (!Files.isDirectory(pdir)) {
        Files.createDirectory(pdir);
      }
      val filename = file.filename
      //val contentType = file.contentType
      
      file.ref.moveTo(pdir.resolve(filename).toFile())

      val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKEY_OF_UPLOAD_FILES)
                          .getOrElse(userCache.set[ListBuffer[OrderItem]](Application.CKEY_OF_UPLOAD_FILES, ListBuffer()).get)+=(OrderItem(filename))

      val json = Json.parse(s"""
        { "files":[{"name":"$filename"}] }
        """)
      val orderPrice = "%.2f".format(allOrderItems.foldLeft(0.0)(_ + _.price))
      
      Ok(json)
      .addingToSession((Application.SKEY_OF_UPLOAD_FILE_COUNT -> allOrderItems.length.toString()))
      .addingToSession((Application.SKEY_OF_ORDER_PRICE -> orderPrice))
      .addingToSession((Application.SKEY_OF_TOTAL_PRICE -> orderPrice))
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
          
    userCache.get[List[Tables.MMaterialRow]](Application.CKEY_OF_TABLE_MMATERIAL) match {
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

    val allOrderItems = userCache.get[ListBuffer[OrderItem]](Application.CKEY_OF_UPLOAD_FILES).getOrElse(ListBuffer())-=(OrderItem(fileName))
    
    val orderPrice = "%.2f".format(allOrderItems.foldLeft(0.0)(_ + _.price))
    Ok("")
    .addingToSession((Application.SKEY_OF_UPLOAD_FILE_COUNT -> allOrderItems.length.toString()))
    .addingToSession((Application.SKEY_OF_ORDER_PRICE -> orderPrice))
    .addingToSession((Application.SKEY_OF_TOTAL_PRICE -> orderPrice))
  }
  
  /**
   * submit items
   */
  def submitItems = CPAction { implicit request =>

    Redirect(routes.Application.editContact);
  }
  
  
  /**
   * edit contact
   */
  def editContact = CPAction { implicit request =>
    Ok(views.html.editContact());
  }
}
