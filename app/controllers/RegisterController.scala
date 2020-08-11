package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class RegisterController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def registerUser = Action {
    Ok(views.html.index("User has been registered!"))
  }

}