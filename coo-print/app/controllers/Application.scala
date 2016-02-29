package controllers


import scala.concurrent.Future
import javax.inject.Inject
import play.api._
import play.api.mvc._
import play.twirl.api._
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import slick.driver.PostgresDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import models.LoginForm
import models.Tables.PUser
import models.Tables.PUserRow
import play.api.i18n.Messages

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.libs.json._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi


class Application @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  object CPAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      Logger.info("Calling action")
      block(request).map { result => 
        val session = request.session
        
        if (session.get("c_id").isEmpty) {
          result.withSession(session + ("c_id", "C1-00002")) 
        }
        result
      }
    }
  }

  
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._
  
  def index = CPAction { request =>
    val result = Ok( views.html.index())
    if (request.session.get("c_id").isEmpty) {
      result.withSession("c_id" -> "C1-00001");
    }
    
    result
  }
  
  
  def shopping = CPAction {
    Ok(views.html.shopping())
  }
  
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
  
  def logout = CPAction {
    Ok(views.html.index());
  }
  
  def upload = CPAction(parse.multipartFormData) { request =>
    request.body.file("files[]").map { file =>
      import java.io.File
      val filename = file.filename
      val contentType = file.contentType
      file.ref.moveTo(new File(s"/tmp/$filename"))
      val json = Json.parse("""
        { "files":[{"name":"abc"}] }
        """)
      Ok(json)
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }

}
