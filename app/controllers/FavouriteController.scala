package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class FavouriteController @Inject()(favouriteRepo: FavouriteRepository, userRepo: UserRepository, productRepo: ProductRepository,
                                    cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val favouriteForm: Form[CreateFavouriteForm] = Form {
    mapping(
      "idUsers" -> number,
      "idProducts" -> number,
    )(CreateFavouriteForm.apply)(CreateFavouriteForm.unapply)
  }

  val updateFavouriteForm: Form[UpdateFavouriteForm] = Form {
    mapping(
      "idFavourites" -> number,
      "idUsers" -> number,
      "idProducts" -> number,
    )(UpdateFavouriteForm.apply)(UpdateFavouriteForm.unapply)
  }

  def getFavourites: Action[AnyContent] = Action.async { implicit request =>
    val favourites = favouriteRepo.list()
    favourites.map(product => Ok(views.html.favourite.favourites(product)))
  }

  def getFavourite(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val setting = favouriteRepo.getByIdOption(id)
    setting.map(favourite => favourite match {
      case Some(s) => Ok(views.html.favourite.favourite(s))
      case None => Redirect(routes.HomeController.index())
    })
  }

  def delete(id: Int) = Action {
    favouriteRepo.delete(id)
    Ok("Successfully removed")
  }

  def updateFavourite(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val products = Await.result(productRepo.list(), Duration.Inf)
    val favourite = Await.result(favouriteRepo.getById(id), Duration.Inf)

    Future.successful {
      val favouriteForm = updateFavouriteForm.fill(UpdateFavouriteForm(
        favourite.idFavourites, favourite.idUsers, favourite.idProducts))
      Ok(views.html.favourite.favouriteupdate(favouriteForm, users, products))
    }
  }

  def updateFavouriteHandle = Action.async { implicit request =>
    var users: Seq[User] = Seq[User]()
    var products: Seq[Product] = Seq[Product]()

    updateFavouriteForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.favourite.favouriteupdate(errorForm, users, products))
        )
      },
      favourite => {
        favouriteRepo.update(favourite.idFavourites, Favourite(
          favourite.idFavourites, favourite.idUsers, favourite.idProducts)).map { _ =>
          Redirect(routes.FavouriteController.updateFavourite(favourite.idFavourites)).flashing("success" -> "favourite updated")
        }
      }
    )
  }

  def addFavourite: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), Duration.Inf)
    val products = Await.result(productRepo.list(), Duration.Inf)

    Future.successful {
      Ok(views.html.favourite.favouriteadd(favouriteForm, users, products))
    }
  }

  def addFavouriteHandle = Action.async { implicit request =>
    var users: Seq[User] = Seq[User]()
    var products: Seq[Product] = Seq[Product]()

    favouriteForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.favourite.favouriteadd(errorForm, users, products))
        )
      },
      favourite => {
        favouriteRepo.create(favourite.idUsers, favourite.idProducts).map { _ =>
          Redirect(routes.FavouriteController.addFavourite()).flashing("success" -> "favourite.created")
        }
      }
    )
  }
}

case class CreateFavouriteForm(idUsers: Int, idProducts: Int)

case class UpdateFavouriteForm(idFavourites: Int, idUsers: Int, idProducts: Int)