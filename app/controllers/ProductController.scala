package controllers

import javax.inject._
import models.{Category, CategoryRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext


@Singleton
class ProductController @Inject()(productsRepo: ProductRepository, categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "idCategories" -> number,
      "price" -> number,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "idProducts" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "idCategories" -> number,
      "price" -> number,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val products = productsRepo.list()
    products.map(products => Ok(views.html.products(products)))
  }

  def getProduct(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val produkt = productsRepo.getByIdOption(id)
    produkt.map(product => product match {
      case Some(p) => Ok(views.html.product(p))
      case None => Redirect(routes.ProductController.getProducts())
    })
  }

  def delete(id: Int): Action[AnyContent] = Action {
    productsRepo.delete(id)
    Redirect("/products")
  }

  def updateProduct(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var categ: Seq[Category] = Seq[Category]()
    val categories = categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }


  val produkt = productsRepo.getById(id)
     produkt.map(product => {
        val prodForm = updateProductForm.fill(UpdateProductForm(product.idProducts, product.name, product.description, product.idCategories, product.price))
        Ok(views.html.productupdate(prodForm, categ))
      })
    }


 	  def updateProductHandle = Action.async { implicit request =>
  var categ: Seq[Category] = Seq[Category]()
  val categories = categoryRepo.list().onComplete {
    case Success(cat) => categ = cat
    case Failure(_) => print("fail")
  }

  updateProductForm.bindFromRequest.fold(
    errorForm => {
      Future.successful(
        BadRequest(views.html.productupdate(errorForm, categ))
      )
    },
    product => {
      productsRepo.update(product.idProducts, Product(product.idProducts, product.name, product.description, product.idCategories, product.price)).map { _ =>
        Redirect(routes.ProductController.updateProduct(product.idProducts)).flashing("success" -> "product updated")
      }
    }
  )
}


  def addProduct: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
    categories.map(cat => Ok(views.html.productadd(productForm, cat)))
  }

  def addProductHandle = Action.async { implicit request =>
    var categ: Seq[Category] = Seq[Category]()
    val categories = categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.productadd(errorForm, categ))
        )
      },
      product => {
        productsRepo.create(product.name, product.description, product.idCategories, product.price).map { _ =>
          Redirect(routes.ProductController.addProduct()).flashing("success" -> "product.created")
        }
      }
    )
  }

}

case class CreateProductForm(name: String, description: String, idCategories: Int, price: Int)

case class UpdateProductForm(idProducts: Int, name: String, description: String, idCategories: Int, price: Int)