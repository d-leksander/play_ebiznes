package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class ProductController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getData = Action {
    Ok(views.html.index("Description about Products!"))
  }

}