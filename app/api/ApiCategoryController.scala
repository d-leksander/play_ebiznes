package api

import controllers.{CreateCategoryForm, UpdateCategoryForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiCategoryController @Inject()(categoryRepo: CategoryRepository,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addCategoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "idCategory" -> number,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      categoryRepo.list().map {
        category => Ok(Json.toJson(category))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val category = for {
        category <- categoryRepo.getByIdOption(id)
      } yield category

      category.map {
        case Some(category) => Ok(Json.toJson(category))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post category!"))
      },
      category => {
        categoryRepo.create(
          category.name
        ).map { category =>
          Created(Json.toJson(category))
        }
      }
    )
  }
}