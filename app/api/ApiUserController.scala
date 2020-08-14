package api

import controllers.{CreateUserForm, UpdateUserForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiUserController @Inject()(userRepo: UserRepository,
                               cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addUserForm: Form[CreateUserForm] = Form {
    mapping(
      "password" -> nonEmptyText,
      "email" -> nonEmptyText,
      "idSettings" -> number,
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "idUser" -> number,
      "password" -> nonEmptyText,
      "email" -> nonEmptyText,
      "idSettings" -> number,
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      userRepo.list().map {
        user => Ok(Json.toJson(user))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val user = for {
        user <- userRepo.getByIdOption(id)
      } yield user

      user.map {
        case Some(user) => Ok(Json.toJson(user))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post user!"))
      },
      user => {
        userRepo.create(
          user.password,
          user.email,
          user.idSettings
        ).map { user =>
          Created(Json.toJson(user))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateUserForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit user."))
          },
          user => {
            userRepo.update(id,
              User(
                user.idUsers,
                user.password,
                user.email,
                user.idSettings
              )).map({ _ =>
              Ok
            })
          }
        )
    }

  def delete(id: Int): Action[AnyContent] = Action {
    userRepo.delete(id)
    Ok("User removed!")
  }
}