package controllers

import java.sql.Date

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

//noinspection MutatorLikeMethodIsParameterless,MutatorLikeMethodIsParameterless,MutatorLikeMethodIsParameterless,MatchToPartialFunction
@Singleton
class OrderController @Inject()(orderRepo: OrderRepository, userRepo: UserRepository, productRepo: ProductRepository,
                                cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val orderForm: Form[CreateOrderForm] = Form {
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

  def getOrders: Action[AnyContent] = Action.async { implicit request =>
    val orders = orderRepo.list()
    orders.map(product => Ok(views.html.order.orders(product)))
  }

  def getOrder(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val setting = orderRepo.getByIdOption(id)
    setting.map(order => order match {
      case Some(s) => Ok(views.html.order.order(s))
      case None => Redirect(routes.HomeController.index())
    })
  }

  def updateOrder(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val products = Await.result(productRepo.list(), Duration.Inf)
    val order = Await.result(orderRepo.getById(id), Duration.Inf)

    Future.successful {
      val orderForm = updateOrderForm.fill(UpdateOrderForm(
        order.idOrders, order.date, order.idUsers, order.idProducts))
      Ok(views.html.order.orderupdate(orderForm, users, products))
    }
  }

  def updateOrderHandle = Action.async { implicit request =>
    var users: Seq[User] = Seq[User]()
    var products: Seq[Product] = Seq[Product]()

    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order.orderupdate(errorForm, users, products))
        )
      },
      order => {
        orderRepo.update(order.idOrders, Order(
          order.idOrders, order.date, order.idUsers, order.idProducts)).map { _ =>
          Redirect(routes.OrderController.updateOrder(order.idOrders)).flashing("success" -> "order updated")
        }
      }
    )
  }

  def addOrder: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val products = Await.result(productRepo.list(), Duration.Inf)

    Future.successful {
      Ok(views.html.order.orderadd(orderForm, users, products))
    }
  }

  def addOrderHandle = Action.async { implicit request =>
    var users: Seq[User] = Seq[User]()
    var products: Seq[Product] = Seq[Product]()

    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order.orderadd(errorForm, users, products))
        )
      },
      order => {
        orderRepo.create(order.date, order.idUsers, order.idProducts).map { _ =>
          Redirect(routes.OrderController.addOrder()).flashing("success" -> "order.created")
        }
      }
    )
  }
}

case class CreateOrderForm(date: Date, idUsers: Int, idProducts: Int)

case class UpdateOrderForm(idOrders: Int, date: Date, idUsers: Int, idProducts: Int)