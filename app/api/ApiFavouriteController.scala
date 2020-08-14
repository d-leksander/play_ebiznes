package api

import controllers.{CreateFavouriteForm, UpdateFavouriteForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiFavouriteController @Inject()(favouriteRepo: FavouriteRepository,
                                    cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addFavouriteForm: Form[CreateFavouriteForm] = Form {
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

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      favouriteRepo.list().map {
        favourite => Ok(Json.toJson(favourite))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val favourite = for {
        favourite <- favouriteRepo.getByIdOption(id)
      } yield favourite

      favourite.map {
        case Some(favourite) => Ok(Json.toJson(favourite))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addFavouriteForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post favourite!"))
      },
      favourite => {
        favouriteRepo.create(
          favourite.idUsers,
          favourite.idProducts
        ).map { favourite =>
          Created(Json.toJson(favourite))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateFavouriteForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit favourite."))
          },
          favourite => {
            favouriteRepo.update(id,
              Favourite(
                favourite.idFavourites,
                favourite.idUsers,
                favourite.idProducts
              )).map({ _ =>
              Ok
            })
          }
        )
    }

  def delete(id: Int): Action[AnyContent] = Action {
    favouriteRepo.delete(id)
    Ok("Favourite removed!")
  }
}