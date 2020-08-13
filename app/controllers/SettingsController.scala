package controllers


import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._


import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SettingsController @Inject()(settingsRepo: SettingsRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val settingsForm: Form[CreateSettingsForm] = Form {
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
  def getSetting(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val setting = settingsRepo.getByIdOption(id)
    setting.map(settings => settings match {
      case Some(s) => Ok(views.html.settings.settings(s))
      case None => Redirect(routes.HomeController.index())
    })
  }
  def updateSettings(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val setting = settingsRepo.getById(id)
    setting.map(settings => {
      val settingsForm = updateSettingsForm.fill(UpdateSettingsForm(
        settings.idSettings, settings.description, settings.age))
      Ok(views.html.settings.settingsupdate(settingsForm))
    })
  }
  def updateSettingsHandle = Action.async { implicit request =>
    updateSettingsForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.settings.settingsupdate(errorForm))
        )
      },
      setting => {
        settingsRepo.update(setting.idSettings, Settings(
          setting.idSettings, setting.description, setting.age)).map { _ =>
          Redirect(routes.SettingsController.updateSettings(setting.idSettings)).flashing("success" -> "settings updated")
        }
      }
    )
  }

  def addSettings: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.settings.settingsadd(settingsForm))
  }

  def addSettingsHandle = Action.async { implicit request =>
    settingsForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.settings.settingsadd(errorForm))
        )
      },
      settings => {
        settingsRepo.create(settings.description, settings.age).map { _ =>
          Redirect(routes.SettingsController.addSettings()).flashing("success" -> "settings.created")
        }
      }
    )
  }
}

case class CreateSettingsForm(description: String, age: Int)

case class UpdateSettingsForm(idSettings: Int, description: String, age: Int)