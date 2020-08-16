package controllers


import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._


import scala.concurrent.{ExecutionContext, Future}

//noinspection MutatorLikeMethodIsParameterless,MutatorLikeMethodIsParameterless,MutatorLikeMethodIsParameterless,MatchToPartialFunction
@Singleton
class UserController @Inject()(userRepo: UserRepository, settingsRepo: SettingsRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
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

  def getUsers: Action[AnyContent] = Action.async { implicit request =>
    val users = userRepo.list()
    users.map(product => Ok(views.html.user.users(product)))
  }

  def getUser(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val setting = userRepo.getByIdOption(id)
    setting.map(user => user match {
      case Some(s) => Ok(views.html.user.user(s))
      case None => Redirect(routes.HomeController.index())
    })
  }

  def updateUser(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var settings: Seq[Settings] = Seq[Settings]()

    val user = userRepo.getById(id)
    user.map(user => {
      val userForm = updateUserForm.fill(UpdateUserForm(
        user.idUsers, user.password, user.email, user.idSettings))
      Ok(views.html.user.userupdate(userForm, settings))
    })
  }
  def updateUserHandle = Action.async { implicit request =>
    var settings: Seq[Settings] = Seq[Settings]()
    updateUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user.userupdate(errorForm, settings))
        )
      },
      user => {
        userRepo.update(user.idUsers, User(
          user.idUsers, user.password, user.email, user.idSettings)).map { _ =>
          Redirect(routes.UserController.updateUser(user.idUsers)).flashing("success" -> "user updated")
        }
      }
    )
  }
  def addUser: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val settings = settingsRepo.list()
    settings.map(s => Ok(views.html.user.useradd(userForm, s)))
  }
  def addUserHandle = Action.async { implicit request =>
    var settings: Seq[Settings] = Seq[Settings]()
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user.useradd(errorForm, settings))
        )
      },
      user => {
        userRepo.create(user.password, user.email, user.idSettings).map { _ =>
          Redirect(routes.UserController.addUser()).flashing("success" -> "user.created")
        }
      }
    )
  }
}

case class CreateUserForm(password: String, email: String, idSettings: Int)

case class UpdateUserForm(idUsers: Int, password: String, email: String, idSettings: Int)