package controllers

import play.api._
import play.api.mvc._
import play.twirl.api._

class Application extends Controller {

  def index = Action {
    Ok(views.html.main("Your new application is ready.")(Html("<h1>hihihi</h1>")))
  }

}
