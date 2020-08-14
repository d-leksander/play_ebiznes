package api

import controllers.{CreateProductForm, UpdateProductForm}
import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiProductController @Inject()(productRepo: ProductRepository,
                                  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val addProductForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "idCategories" -> number,
      "price" -> number,
      "idDelivery" -> number,
      "idPhotos" -> number,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "idProducts" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "idCategories" -> number,
      "price" -> number,
      "idDelivery" -> number,
      "idPhotos" -> number,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def all: Action[AnyContent] = {
    Action.async { implicit request =>
      productRepo.list().map {
        product => Ok(Json.toJson(product))
      }
    }
  }

  def get(id: Int): Action[AnyContent] = {
    Action.async { implicit request =>
      val product = for {
        product <- productRepo.getByIdOption(id)
      } yield product

      product.map {
        case Some(product) => Ok(Json.toJson(product))
        case None => NotFound
      }
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request =>
    addProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("Failed to post product!"))
      },
      product => {
        productRepo.create(
          product.name,
          product.description,
          product.idCategories,
          product.price,
          product.idDelivery,
          product.idPhotos
        ).map { product =>
          Created(Json.toJson(product))
        }
      }
    )
  }

  def edit(id: Int): Action[JsValue] =
    Action.async(parse.json) {
      implicit request =>
        updateProductForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("Failed to edit product."))
          },
          product => {
            productRepo.update(id,
              Product(
                product.idProducts,
                product.name,
                product.description,
                product.idCategories,
                product.price,
                product.idDelivery,
                product.idPhotos)).map({ _ =>
              Ok
            })
          }
        )
    }

  def delete(id: Int): Action[AnyContent] = Action {
    productRepo.delete(id)
    Ok("Product removed!")
  }
}