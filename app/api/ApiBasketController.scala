package api

import controllers.{CreateBasketForm, UpdateBasketForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiBasketController @Inject()(basketRepo: BasketRepository,
                                 cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addBasketForm: Form[CreateBasketForm] = Form {
    mapping(
      "idUsers" -> nonEmptyText,
      "idProducts" -> number,
    )(CreateBasketForm.apply)(CreateBasketForm.unapply)
  }

  val updateBasketForm: Form[UpdateBasketForm] = Form {
    mapping(
      "idBaskets" -> number,
      "idUsers" -> nonEmptyText,
      "idProducts" -> number,
    )(UpdateBasketForm.apply)(UpdateBasketForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      basketRepo.list().map {
        basket => Ok(Json.toJson(basket))
      }
    }
  }

  def getByUserId(id: String): Action[AnyContent] = {
    Action.async { implicit request =>
      basketRepo.getByUser(id).map {
        basket => Ok(Json.toJson(basket))
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addBasketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post basket!"))
      },
      basket => {
        basketRepo.create(
          basket.idUsers,
          basket.idProducts
        ).map { basket =>
          Created(Json.toJson(basket))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateBasketForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit basket."))
          },
          basket => {
            basketRepo.update(id,
              Basket(
                basket.idBaskets,
                basket.idUsers,
                basket.idProducts
              )).map({ _ =>
              Ok
            })
          }
        )
    }

  def delete(id: Int): Action[AnyContent] = Action {
    basketRepo.delete(id)
    Ok("Basket removed!")
  }
}