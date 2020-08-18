package api

import controllers.{CreateOrderForm, UpdateOrderForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(orderRepo: OrderRepository,
                                cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addOrderForm: Form[CreateOrderForm] = Form {
    mapping(
      "date" -> sqlDate,
      "idUsers" -> number,
      "idProducts" -> number,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "idOrders" -> number,
      "date" -> sqlDate,
      "idUsers" -> number,
      "idProducts" -> number,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      orderRepo.list().map {
        order => Ok(Json.toJson(order))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val order = for {
        order <- orderRepo.getByIdOption(id)
      } yield order

      order.map {
        case Some(order) => Ok(Json.toJson(order))
        case None => NotFound
      }
    }
  }

  def getByUserId(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      orderRepo.getByUser(id).map {
        order => Ok(Json.toJson(order))
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post order!"))
      },
      order => {
        orderRepo.create(
          order.date,
          order.idUsers,
          order.idProducts
        ).map { order =>
          Created(Json.toJson(order))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateOrderForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit order."))
          },
          order => {
            orderRepo.update(id,
              Order(
                order.idOrders,
                order.date,
                order.idUsers,
                order.idProducts
              )).map({ _ =>
              Ok
            })
          }
        )
    }
}