package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val product = TableQuery[ProductTable]

  def create(name: String, description: String, idCategories: Int, price: Int, idDelivery: Int, idPhotos: Int): Future[Product] = db.run {
    (product.map(p => (p.name, p.description, p.idCategories, p.price, p.idDelivery, p.idPhotos))

      returning product.map(_.idProducts)
      into { case ((name, description, idCategories, price, idDelivery, idPhotos), idProducts) => Product(idProducts, name, description, idCategories, price, idDelivery, idPhotos) }
      ) += (name, description, idCategories, price, idDelivery, idPhotos)
  }

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getByCategory(category_id: Int): Future[Seq[Product]] = db.run {
    product.filter(_.idCategories === category_id).result
  }

  def getById(idProducts: Int): Future[Product] = db.run {
    product.filter(_.idProducts === idProducts).result.head
  }

  def getByIdOption(idProducts: Int): Future[Option[Product]] = db.run {
    product.filter(_.idProducts === idProducts).result.headOption
  }

  def getByCategories(category_ids: List[Int]): Future[Seq[Product]] = db.run {
    product.filter(_.idCategories inSet category_ids).result
  }

  def delete(idProducts: Int): Future[Unit] = db.run(product.filter(_.idProducts === idProducts).delete).map(_ => ())

  def update(id: Int, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(product.filter(_.idProducts === id).update(productToUpdate)).map(_ => ())
  }
}