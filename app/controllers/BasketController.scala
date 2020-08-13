package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
@Singleton
class BasketController @Inject()(basketRepo: BasketRepository, userRepo: UserRepository, productRepo: ProductRepository,
                                 cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val basketForm: Form[CreateBasketForm] = Form {
    mapping(
      "idUsers" -> number,
      "idProducts" -> number,
    )(CreateBasketForm.apply)(CreateBasketForm.unapply)
  }

  val deleteBasketForm: Form[DeleteBasketForm] = Form {
    mapping(
      "idBaskets" -> number,
    )(DeleteBasketForm.apply)(DeleteBasketForm.unapply)
  }

  val updateBasketForm: Form[UpdateBasketForm] = Form {
    mapping(
      "idBaskets" -> number,
      "idUsers" -> number,
      "idProducts" -> number,
    )(UpdateBasketForm.apply)(UpdateBasketForm.unapply)
  }

  def getBaskets: Action[AnyContent] = Action.async { implicit request =>
    val baskets = basketRepo.list()
    baskets.map(product => Ok(views.html.basket.baskets(product)))
  }

  def getBasket(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val setting = basketRepo.getByIdOption(id)
    setting.map(basket => basket match {
      case Some(s) => Ok(views.html.basket.basket(s))
      case None => Redirect(routes.HomeController.index())
    })
  }

  def deleteBasket(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val basket = Await.result(basketRepo.getById(id), Duration.Inf)

    Future.successful {
      val basketForm = deleteBasketForm.fill(DeleteBasketForm(
        basket.idBaskets))
      Ok(views.html.basket.basketdelete(basketForm))
    }
  }
  def deleteBasketHandle = Action.async { implicit request =>
    deleteBasketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.basket.basketdelete(errorForm))
        )
      },
      basket => {
        basketRepo.delete(basket.idBaskets).map { _ =>
          Redirect(routes.BasketController.deleteBasket(basket.idBaskets)).flashing("success" -> "basket deleted")
        }
      }
    )
  }
  def delete(id: Int) = Action {
    basketRepo.delete(id)
    Ok("Successfully removed")
  }
  def updateBasket(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val products = Await.result(productRepo.list(), Duration.Inf)
    val basket = Await.result(basketRepo.getById(id), Duration.Inf)

    Future.successful {
      val basketForm = updateBasketForm.fill(UpdateBasketForm(
        basket.idBaskets, basket.idUsers, basket.idProducts))
      Ok(views.html.basket.basketupdate(basketForm, users, products))
    }
  }
  def updateBasketHandle = Action.async { implicit request =>
    var users: Seq[User] = Seq[User]()
    var products: Seq[Product] = Seq[Product]()

    updateBasketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.basket.basketupdate(errorForm, users, products))
        )
      },
      basket => {
        basketRepo.update(basket.idBaskets, Basket(
          basket.idBaskets, basket.idUsers, basket.idProducts)).map { _ =>
          Redirect(routes.BasketController.updateBasket(basket.idBaskets)).flashing("success" -> "basket updated")
        }
      }
    )
  }

  def addBasket: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val products = Await.result(productRepo.list(), Duration.Inf)

    Future.successful {
      Ok(views.html.basket.basketadd(basketForm, users, products))
    }
  }

  def addBasketHandle = Action.async { implicit request =>
    var users: Seq[User] = Seq[User]()
    var products: Seq[Product] = Seq[Product]()

    basketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.basket.basketadd(errorForm, users, products))
        )
      },
      basket => {
        basketRepo.create(basket.idUsers, basket.idProducts).map { _ =>
          Redirect(routes.BasketController.addBasket()).flashing("success" -> "basket.created")
        }
      }
    )
  }
}
case class CreateBasketForm(idUsers: Int, idProducts: Int)

case class DeleteBasketForm(idBaskets: Int)

case class UpdateBasketForm(idBaskets: Int, idUsers: Int, idProducts: Int)
