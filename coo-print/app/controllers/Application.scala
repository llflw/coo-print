package controllers

import play.api._
import play.api.mvc._
import play.twirl.api._
import play.api.libs.json._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."));
    //Ok("Test");
  }
  
  
  def main = Action {
    Ok(views.html.main("Your new application is ready.")(Html("<h1>hihihi</h1>")))
  }
  
  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("files").map { file =>
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
