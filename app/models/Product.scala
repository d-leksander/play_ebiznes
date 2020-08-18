package models

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Product(idProducts: Int, name: String, description: String, idCategories: Int, price: Int, idDelivery: Int, idPhotos: Int)

class ProductTable(tag: Tag) extends Table[Product](tag, "Products") {
  val category = TableQuery[CategoryTable]
  val photo = TableQuery[PhotoTable]
  val delivery = TableQuery[DeliveryTable]

  def idCategoriesFk = foreignKey("cat_fk", idCategories, category)(_.idCategories)

  def idCategories = column[Int]("idCategories")

  def idDeliveryFk = foreignKey("del_fk", idDelivery, delivery)(_.idDelivery)

  def idDelivery = column[Int]("idDelivery")

  def idPhotosFk = foreignKey("pho_fk", idPhotos, photo)(_.idPhotos)

  def idPhotos = column[Int]("idPhotos")

  def * = (idProducts, name, description, idCategories, price, idDelivery, idPhotos) <> ((Product.apply _).tupled, Product.unapply)

  def idProducts = column[Int]("idProducts", O.PrimaryKey, O.AutoInc, O.Unique)

  def name = column[String]("name")

  def description = column[String]("description")

  def price = column[Int]("price")
}

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}