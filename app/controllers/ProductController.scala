package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json.Json
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


@Singleton
class ProductController @Inject()(productsRepo: ProductRepository, categoryRepo: CategoryRepository, photoRepo: PhotoRepository, deliveryRepo: DeliveryRepository,
                                  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
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

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val products = productsRepo.list()
    products.map(products => Ok(views.html.product.products(products)))
  }

  def getProduct(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val produkt = productsRepo.getByIdOption(id)
    produkt.map(product => product match {
      case Some(p) => Ok(views.html.product.product(p))
      case None => Redirect(routes.ProductController.getProducts())
    })
  }

  def delete(id: Int): Action[AnyContent] = Action {
    productsRepo.delete(id)
    Redirect("/products")
  }

  def updateProduct(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val photos = Await.result(photoRepo.list(), Duration.Inf)
    val deliverys = Await.result(deliveryRepo.list(), Duration.Inf)
    val categories = Await.result(categoryRepo.list(), Duration.Inf)
    val product = Await.result(productsRepo.getById(id), Duration.Inf)
    Future.successful {
      val prodForm = updateProductForm.fill(UpdateProductForm(
        product.idProducts, product.name, product.description, product.idCategories, product.price, product.idDelivery, product.idPhotos))
      Ok(views.html.product.productupdate(prodForm, categories, deliverys, photos))

  }

  }


 	  def updateProductHandle = Action.async { implicit request =>
      val categ: Seq[Category] = Seq[Category]()
      val photos: Seq[Photo] = Seq[Photo]()
      val deliverys: Seq[Delivery] = Seq[Delivery]()

  updateProductForm.bindFromRequest.fold(
    errorForm => {
      Future.successful(
        BadRequest(views.html.product.productupdate(errorForm, categ, deliverys, photos))
      )
    },
    product => {
      productsRepo.update(product.idProducts, Product(product.idProducts, product.name, product.description, product.idCategories, product.price, product.idDelivery, product.idPhotos)).map { _ =>
        Redirect(routes.ProductController.updateProduct(product.idProducts)).flashing("success" -> "product updated")
      }
    }
  )
}


  def addProduct: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val photos = Await.result(photoRepo.list(), Duration.Inf)
    val deliverys = Await.result(deliveryRepo.list(), Duration.Inf)
    val categories = Await.result(categoryRepo.list(), Duration.Inf)
    Future.successful {
    Ok(views.html.product.productadd(productForm, categories, deliverys, photos))
  }

  }

  def addProductHandle = Action.async { implicit request =>
    val photos = Await.result(photoRepo.list(), Duration.Inf)
    val deliverys = Await.result(deliveryRepo.list(), Duration.Inf)
    val categ = Await.result(categoryRepo.list(), Duration.Inf)


    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product.productadd(errorForm, categ, deliverys, photos))
        )
      },
      product => {
        productsRepo.create(product.name, product.description, product.idCategories, product.price, product.idDelivery, product.idPhotos).map { _ =>
          Redirect(routes.ProductController.addProduct()).flashing("success" -> "product.created")
        }
      }
    )
  }

  def getAll: Action[AnyContent] = {
    Action.async { implicit request =>
      productsRepo.list().map {
        product => Ok(Json.toJson(product))
      }
    }
  }

}

case class CreateProductForm(name: String, description: String, idCategories: Int, price: Int, idDelivery: Int, idPhotos: Int)

case class UpdateProductForm(idProducts: Int, name: String, description: String, idCategories: Int, price: Int, idDelivery: Int, idPhotos: Int)