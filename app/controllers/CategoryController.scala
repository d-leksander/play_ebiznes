package controllers

import javax.inject._
import models.CategoryRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepository,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
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

  def getCategory(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val category = categoryRepo.getByIdOption(id)
    category.map(category => category match {
      case Some(c) => Ok(views.html.category.category(c))
      case None => Redirect(routes.HomeController.index())
    })
  }

  def getCategorys: Action[AnyContent] = Action.async { implicit request =>
    val cetegorys = categoryRepo.list()
    cetegorys.map(c => Ok(views.html.category.categorys(c)))
  }

  def addCategory: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.category.categoryadd(categoryForm))
  }

  def addCategoryHandle = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category.categoryadd(errorForm))
        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(routes.CategoryController.addCategory()).flashing("success" -> "category.created")
        }
      }
    )
  }
}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(idCategories: Int, name: String)