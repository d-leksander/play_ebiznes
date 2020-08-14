package api

import controllers.{CreateSettingsForm, UpdateSettingsForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiSettingsController @Inject()(settingsRepo: SettingsRepository,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addSettingsForm: Form[CreateSettingsForm] = Form {
    mapping(
      "description" -> nonEmptyText,
      "age" -> number,
    )(CreateSettingsForm.apply)(CreateSettingsForm.unapply)
  }

  val updateSettingsForm: Form[UpdateSettingsForm] = Form {
    mapping(
      "idSettings" -> number,
      "description" -> nonEmptyText,
      "age" -> number,
    )(UpdateSettingsForm.apply)(UpdateSettingsForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      settingsRepo.list().map {
        settings => Ok(Json.toJson(settings))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val settings = for {
        settings <- settingsRepo.getByIdOption(id)
      } yield settings

      settings.map {
        case Some(settings) => Ok(Json.toJson(settings))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addSettingsForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post settings!"))
      },
      settings => {
        settingsRepo.create(
          settings.description,
          settings.age
        ).map { settings =>
          Created(Json.toJson(settings))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateSettingsForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit settings."))
          },
          settings => {
            settingsRepo.update(id,
              Settings(
                settings.idSettings,
                settings.description,
                settings.age
              )).map({ _ =>
              Ok
            })
          }
        )
    }
}