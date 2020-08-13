package controllers

import javax.inject._
import models.{Photo, PhotoRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PhotoController @Inject()(photosRepo: PhotoRepository,
                                cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val photoForm: Form[CreatePhotoForm] = Form {
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

  def getPhotos: Action[AnyContent] = Action.async { implicit request =>
    val photos = photosRepo.list()
    photos.map(photos => Ok(views.html.photo.photos(photos)))
  }

  def getPhoto(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val photo = photosRepo.getByIdOption(id)
    photo.map(photo => photo match {
      case Some(p) => Ok(views.html.photo.photo(p))
      case None => Redirect(routes.PhotoController.getPhotos())
    })
  }

  def delete(id: Int): Action[AnyContent] = Action {
    photosRepo.delete(id)
    Redirect("/photos")
  }

  def updatePhoto(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val photo = photosRepo.getById(id)
    photo.map(photo => {
      val prodForm = updatePhotoForm.fill(UpdatePhotoForm(
        photo.idPhotos, photo.path))
      Ok(views.html.photo.photoupdate(prodForm))
    })
  }

  def updatePhotoHandle = Action.async { implicit request =>
    updatePhotoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.photo.photoupdate(errorForm))
        )
      },
      photo => {
        photosRepo.update(photo.idPhotos, Photo(
          photo.idPhotos, photo.path)).map { _ =>
          Redirect(routes.PhotoController.updatePhoto(photo.idPhotos)).flashing("success" -> "photo updated")
        }
      }
    )
  }

  def addPhoto: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.photo.photoadd(photoForm))
  }

  def addPhotoHandle = Action.async { implicit request =>
    photoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.photo.photoadd(errorForm))
        )
      },
      photo => {
        photosRepo.create(photo.path).map { _ =>
          Redirect(routes.PhotoController.addPhoto()).flashing("success" -> "photo.created")
        }
      }
    )
  }
}

case class CreatePhotoForm(path: String)

case class UpdatePhotoForm(idPhotos: Int, path: String)