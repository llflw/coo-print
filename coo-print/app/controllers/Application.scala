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
import models.Tables.MMaterialRow
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

object Application {
  
  val SKEY_OF_CLIENT_ID = "c_id"
  val SKEY_OF_UPLOAD_FILE_COUNT = "uploadFileCount"
  val SKEY_OF_ORDER_PRICE = "orderPrice"
  val SKEY_OF_TOTAL_PRICE = "totalPrice"
  
  val CKEY_OF_UPLOAD_FILES = "-uploadFiles"
  val CKEY_OF_TABLE_MMATERIAL = "table_m_material"
}

class Application @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val messagesApi: MessagesApi, val cache: CacheApi) extends Controller with I18nSupport {

  
  object CPAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      import java.time.LocalDateTime
      Logger.info("Calling action:" + request.path + " - " + LocalDateTime.now())
      block(request).map { result => 
        if (request.session.get(Application.SKEY_OF_CLIENT_ID).isEmpty) {
          result.withSession(request.session + (Application.SKEY_OF_CLIENT_ID -> UUID.randomUUID().toString()))
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
    
    val cachedMaterials = cache.getOrElse[List[MMaterialRow]](Application.CKEY_OF_TABLE_MMATERIAL){
      val materialRows : Future[Seq[MMaterialRow]] = dbConfig.db.run(MMaterial.result)
      val materials = Await.result(materialRows, Duration(1, "sec")).toList
      cache.set(Application.CKEY_OF_TABLE_MMATERIAL, materials)
      materials
    }
   
        
    val c_id = request.session.get(Application.SKEY_OF_CLIENT_ID).get
    val orderItemList = cache.get[ListBuffer[OrderItem]](c_id + Application.CKEY_OF_UPLOAD_FILES).getOrElse(ListBuffer())
    Ok(views.html.editItems(orderItemList))
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
      
      val c_id = request.session.get(Application.SKEY_OF_CLIENT_ID).get
      val pdir = Paths.get(uploadFileDir, c_id)
      
      if (!Files.isDirectory(pdir)) {
        Files.createDirectory(pdir);
      }
      val filename = file.filename
      //val contentType = file.contentType
      
      file.ref.moveTo(pdir.resolve(filename).toFile())

      val uploadFilesKey = c_id+Application.CKEY_OF_UPLOAD_FILES
      val allOrderItems = cache.get[ListBuffer[OrderItem]](uploadFilesKey).getOrElse(ListBuffer())+=(OrderItem(filename))
      cache.set(uploadFilesKey, allOrderItems);
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
  def props(mid : String) = Action {
    val defaultJson = Json.parse("""{
         "color" : [""], "finish" : [""],"fill" : [""],"layer" : [""],"zoom" : [""]
          }""")
          
    cache.get[List[MMaterialRow]](Application.CKEY_OF_TABLE_MMATERIAL) match {
      case Some(materials) => 
        val filteredMrs = materials.filter { mr => mr.materialId == mid.toInt }
        if (filteredMrs.length > 0) {
         val fmr = filteredMrs(0)
         val json = Json.obj(
         "color" -> fmr.vColor.split(","),  
         "finish" -> fmr.vFinish.split(","),
         "fill" -> fmr.vFill.split(","),
         "layer" -> fmr.vLayer.split(","),
         "zoom" -> fmr.vZoom.split(",")
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
    val c_id = request.session.get(Application.SKEY_OF_CLIENT_ID).get
    val uploadFilesKey = c_id+Application.CKEY_OF_UPLOAD_FILES
    val allOrderItems = cache.get[ListBuffer[OrderItem]](uploadFilesKey).getOrElse(ListBuffer())-=(OrderItem(fileName))
    cache.set(uploadFilesKey, allOrderItems);
    
    val orderPrice = "%.2f".format(allOrderItems.foldLeft(0.0)(_ + _.price))
    Ok("")
    .addingToSession((Application.SKEY_OF_UPLOAD_FILE_COUNT -> allOrderItems.length.toString()))
    .addingToSession((Application.SKEY_OF_ORDER_PRICE -> orderPrice))
    .addingToSession((Application.SKEY_OF_TOTAL_PRICE -> orderPrice))
  }
  
  /**
   * submit order
   */
  def submitOrder = CPAction { implicit request =>
    Ok("abc");
  }

}
