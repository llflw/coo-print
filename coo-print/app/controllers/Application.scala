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


class Application @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val messagesApi: MessagesApi, val cache: CacheApi) extends Controller with I18nSupport {

  val KEY_OF_CLIENT_ID = "c_id"
  val SUBKEY_OF_UPLOAD_FILES = "-uploadFiles"
  val KEY_OF_TABLE_MMATERIAL = "t_m_material"
  
  object CPAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      import java.time.LocalDateTime
      Logger.info("Calling action:" + request.path + " - " + LocalDateTime.now())
      block(request).map { result => 
        val session = request.session
        if (session.get(KEY_OF_CLIENT_ID).isEmpty) {
          result.withSession(session + (KEY_OF_CLIENT_ID, UUID.randomUUID().toString()));
        } else {
          result
        }
      }
    }
  }

  
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  
  /**
   * main page action
   */
  def index = CPAction { 
    Ok( views.html.index())
  }
  
  
  def shopping = CPAction { implicit request =>
    
    val cachedMaterials = cache.getOrElse[List[MMaterialRow]](KEY_OF_TABLE_MMATERIAL){
      val materialRows : Future[Seq[MMaterialRow]] = dbConfig.db.run(MMaterial.result)
      val materials = Await.result(materialRows, Duration(1, "sec")).toList
      cache.set(KEY_OF_TABLE_MMATERIAL, materials)
      materials
    }
   
        
    val c_id = request.session.get(KEY_OF_CLIENT_ID).get
    val orderItemList = cache.get[ListBuffer[OrderItem]](c_id + SUBKEY_OF_UPLOAD_FILES).getOrElse(ListBuffer())
    Ok(views.html.shopping(orderItemList))
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
        Ok(Json.obj(
          "status" -> "ok",
          "msg" -> JsString(users(0).userName.get + ", 登录中！")
        ));
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
  def logout = CPAction {
    Redirect(routes.Application.index)
  }
  
  /**
   * file upload action
   */
  def upload = CPAction(parse.multipartFormData) {implicit request =>
    
    if (request.session.get(KEY_OF_CLIENT_ID).isEmpty) {
      Redirect(routes.Application.index)
    } else {
      
      request.body.file("files[]").map { file =>
        import java.io.File
        import java.nio.file.Path
        import java.nio.file.Files
        import java.nio.file.Paths
        
        val uploadFileDir = Play.current.configuration.getString("cp.upload.dir").getOrElse("/tmp")
        
        val c_id = request.session.get(KEY_OF_CLIENT_ID).get
        val pdir = Paths.get(uploadFileDir, c_id)
        
        if (!Files.isDirectory(pdir)) {
          try{
          Files.createDirectory(pdir);
          } catch {
            case _ : Throwable => 
          }
        }
        val filename = file.filename
       // val contentType = file.contentType
        
        file.ref.moveTo(pdir.resolve(filename).toFile())

        val uploadFilesKey = c_id+SUBKEY_OF_UPLOAD_FILES
        cache.set(uploadFilesKey, cache.get[ListBuffer[OrderItem]](uploadFilesKey).getOrElse(ListBuffer())+=(OrderItem(filename)));
        val json = Json.parse(s"""
          { "files":[{"name":"$filename"}] }
          """)
        Ok(json)
      }.getOrElse {
        Redirect(routes.Application.index).flashing(
          "error" -> "Missing file")
      }
    }
  }

}
