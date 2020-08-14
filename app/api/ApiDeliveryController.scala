package api

import controllers.{CreateDeliveryForm, UpdateDeliveryForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiDeliveryController @Inject()(deliveryRepo: DeliveryRepository,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addDeliveryForm: Form[CreateDeliveryForm] = Form {
    mapping(
      "type" -> nonEmptyText,
      "price" -> number,
    )(CreateDeliveryForm.apply)(CreateDeliveryForm.unapply)
  }

  val updateDeliveryForm: Form[UpdateDeliveryForm] = Form {
    mapping(
      "idDelivery" -> number,
      "path" -> nonEmptyText,
      "price" -> number,
    )(UpdateDeliveryForm.apply)(UpdateDeliveryForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      deliveryRepo.list().map {
        delivery => Ok(Json.toJson(delivery))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val delivery = for {
        delivery <- deliveryRepo.getByIdOption(id)
      } yield delivery

      delivery.map {
        case Some(delivery) => Ok(Json.toJson(delivery))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addDeliveryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post delivery!"))
      },
      delivery => {
        deliveryRepo.create(
          delivery.company,
          delivery.price
        ).map { delivery =>
          Created(Json.toJson(delivery))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateDeliveryForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit delivery."))
          },
          delivery => {
            deliveryRepo.update(id,
              Delivery(
                delivery.idDelivery,
                delivery.company,
                delivery.price
              )).map({ _ =>
              Ok
            })
          }
        )
    }

  def delete(id: Int): Action[AnyContent] = Action {
    deliveryRepo.delete(id)
    Ok("Delivery removed!")
  }
}