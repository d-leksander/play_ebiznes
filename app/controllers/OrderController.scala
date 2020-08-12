package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._

@Singleton
class OrdersController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def  getMyOrders = Action {
    Ok(views.html.index("Your orders"))
  }
}