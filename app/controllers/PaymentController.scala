package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class PaymentController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def pay = Action {
    Ok(views.html.index("Payment complete!"))
  }

}