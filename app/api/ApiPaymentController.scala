package api

import controllers.{CreatePaymentForm, UpdatePaymentForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiPaymentController @Inject()(paymentRepo: PaymentRepository,
                                  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addPaymentForm: Form[CreatePaymentForm] = Form {
    mapping(
      "status" -> nonEmptyText,
      "date" -> sqlDate,
      "idUsers" -> number,
      "value" -> number,
    )(CreatePaymentForm.apply)(CreatePaymentForm.unapply)
  }

  val updatePaymentForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "idPayments" -> number,
      "status" -> nonEmptyText,
      "date" -> sqlDate,
      "idUsers" -> number,
      "value" -> number,
    )(UpdatePaymentForm.apply)(UpdatePaymentForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      paymentRepo.list().map {
        payment => Ok(Json.toJson(payment))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val payment = for {
        payment <- paymentRepo.getByIdOption(id)
      } yield payment

      payment.map {
        case Some(payment) => Ok(Json.toJson(payment))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addPaymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post payment!"))
      },
      payment => {
        paymentRepo.create(
          payment.status,
          payment.date,
          payment.idUsers,
          payment.value
        ).map { payment =>
          Created(Json.toJson(payment))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updatePaymentForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit payment."))
          },
          payment => {
            paymentRepo.update(id,
              Payment(
                payment.idPayments,
                payment.status,
                payment.date,
                payment.idUsers,
                payment.value
              )).map({ _ =>
              Ok
            })
          }
        )
    }
}