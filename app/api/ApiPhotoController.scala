package api

import controllers.{CreatePhotoForm, UpdatePhotoForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiPhotoController @Inject()(photoRepo: PhotoRepository,
                                cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addPhotoForm: Form[CreatePhotoForm] = Form {
    mapping(
      "path" -> nonEmptyText,
    )(CreatePhotoForm.apply)(CreatePhotoForm.unapply)
  }

  val updatePhotoForm: Form[UpdatePhotoForm] = Form {
    mapping(
      "idPhotos" -> number,
      "path" -> nonEmptyText,
    )(UpdatePhotoForm.apply)(UpdatePhotoForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      photoRepo.list().map {
        photo => Ok(Json.toJson(photo))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val photo = for {
        photo <- photoRepo.getByIdOption(id)
      } yield photo

      photo.map {
        case Some(photo) => Ok(Json.toJson(photo))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addPhotoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post photo!"))
      },
      photo => {
        photoRepo.create(
          photo.path
        ).map { photo =>
          Created(Json.toJson(photo))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updatePhotoForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit photo."))
          },
          photo => {
            photoRepo.update(id,
              Photo(
                photo.idPhotos,
                photo.path
              )).map({ _ =>
              Ok
            })
          }
        )
    }

  def delete(id: Int): Action[AnyContent] = Action {
    photoRepo.delete(id)
    Ok("Photo removed!")
  }
}