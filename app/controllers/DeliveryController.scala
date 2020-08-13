package controllers

import javax.inject._
import models.{Delivery, DeliveryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeliveryController @Inject()(deliveryRepo: DeliveryRepository,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val deliveryForm: Form[CreateDeliveryForm] = Form {
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

  def getDeliverys: Action[AnyContent] = Action.async { implicit request =>
    val deliverys = deliveryRepo.list()
    deliverys.map(deliverys => Ok(views.html.delivery.deliverys(deliverys)))
  }

  def getDelivery(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val delivery = deliveryRepo.getByIdOption(id)
    delivery.map(delivery => delivery match {
      case Some(d) => Ok(views.html.delivery.delivery(d))
      case None => Redirect(routes.DeliveryController.getDeliverys())
    })
  }

  def delete(id: Int): Action[AnyContent] = Action {
    deliveryRepo.delete(id)
    Redirect("/deliverys")
  }

  def updateDelivery(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val delivery = deliveryRepo.getById(id)
    delivery.map(delivery => {
      val prodForm = updateDeliveryForm.fill(UpdateDeliveryForm(
        delivery.idDelivery, delivery.company, delivery.price))
      Ok(views.html.delivery.deliveryupdate(prodForm))
    })
  }

  def updateDeliveryHandle = Action.async { implicit request =>
    updateDeliveryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.delivery.deliveryupdate(errorForm))
        )
      },
      delivery => {
        deliveryRepo.update(delivery.idDelivery, Delivery(
          delivery.idDelivery, delivery.company, delivery.price)).map { _ =>
          Redirect(routes.DeliveryController.updateDelivery(delivery.idDelivery)).flashing("success" -> "delivery updated")
        }
      }
    )
  }

  def addDelivery: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.delivery.deliveryadd(deliveryForm))
  }

  def addDeliveryHandle = Action.async { implicit request =>
    deliveryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.delivery.deliveryadd(errorForm))
        )
      },
      delivery => {
        deliveryRepo.create(delivery.company, delivery.price).map { _ =>
          Redirect(routes.DeliveryController.addDelivery()).flashing("success" -> "delivery.created")
        }
      }
    )
  }
}

case class CreateDeliveryForm(company: String, price: Int)

case class UpdateDeliveryForm(idDelivery: Int, company: String, price: Int)