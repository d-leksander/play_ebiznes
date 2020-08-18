
package controllers


import java.sql.Date

import javax.inject._
import models.{Payment, PaymentRepository, UserRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._


import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

//noinspection MutatorLikeMethodIsParameterless,MutatorLikeMethodIsParameterless,MutatorLikeMethodIsParameterless,MatchToPartialFunction
@Singleton
class PaymentController @Inject()(paymentRepo: PaymentRepository, userRepo: UserRepository,
                                  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val paymentForm: Form[CreatePaymentForm] = Form {
    mapping(
      "status" -> nonEmptyText,
      "date" -> sqlDate,
      "idUsers" -> nonEmptyText,
      "value" -> number,
    )(CreatePaymentForm.apply)(CreatePaymentForm.unapply)
  }

  val updatePaymentForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "idPayments" -> number,
      "status" -> nonEmptyText,
      "date" -> sqlDate,
      "idUsers" -> nonEmptyText,
      "value" -> number,
    )(UpdatePaymentForm.apply)(UpdatePaymentForm.unapply)
  }

  def getPayments: Action[AnyContent] = Action.async { implicit request =>
    val payments = paymentRepo.list()
    payments.map(product => Ok(views.html.payment.payments(product)))
  }

  def getPayment(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val setting = paymentRepo.getByIdOption(id)
    setting.map(payment => payment match {
      case Some(s) => Ok(views.html.payment.payment(s))
      case None => Redirect(routes.HomeController.index())
    })
  }

  def updatePayment(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val payment = Await.result(paymentRepo.getById(id), Duration.Inf)
    Future.successful {
      val paymentForm = updatePaymentForm.fill(UpdatePaymentForm(
        payment.idPayments, payment.status, payment.date, payment.idUsers, payment.value))
      Ok(views.html.payment.paymentupdate(paymentForm, users))
    }
  }
  def updatePaymentHandle = Action.async { implicit request =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    updatePaymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment.paymentupdate(errorForm, users))
        )
      },
      payment => {
        paymentRepo.update(payment.idPayments, Payment(
          payment.idPayments, payment.status, payment.date, payment.idUsers, payment.value)).map { _ =>
          Redirect(routes.PaymentController.updatePayment(payment.idPayments)).flashing("success" -> "payment.updated")
        }
      }
    )
  }

  def addPayment: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    Future.successful {
      Ok(views.html.payment.paymentadd(paymentForm, users))
    }
  }

  def addPaymentHandle = Action.async { implicit request =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    paymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment.paymentadd(errorForm, users))
        )
      },
      payment => {
        paymentRepo.create(payment.status, payment.date, payment.idUsers, payment.value).map { _ =>
          Redirect(routes.PaymentController.addPayment()).flashing("success" -> "payment.created")
        }
      }
    )
  }
}

case class CreatePaymentForm(status: String, date: Date, idUsers: String, value: Int)

case class UpdatePaymentForm(idPayments: Int, status: String, date: Date, idUsers: String, value: Int)



