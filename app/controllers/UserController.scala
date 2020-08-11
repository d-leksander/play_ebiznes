package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getProfile = Action {
    Ok(views.html.index("Your profile"))
  }

  def updateProfile = Action {
    Ok(views.html.index("Your profile has been updated"))
  }
}