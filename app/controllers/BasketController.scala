package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._

@Singleton
class BasketController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def  getProducts = Action {
    Ok(views.html.index("Basket"))
  }

  def  removeFromBasket = Action {
    Ok(views.html.index("Delete from Basket"))
  }

  def  addToBasket = Action {
    Ok(views.html.index("Added to Basket"))
  }

}