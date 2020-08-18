package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Basket(idBaskets: Int, idUsers: String, idProducts: Int)

class BasketTable(tag: Tag) extends Table[Basket](tag, "Baskets") {
  val user = TableQuery[UserTable]
  val product = TableQuery[ProductTable]

  def idUsersFk = foreignKey("usr_fk", idUsers, user)(_.id)

  def idProductsFk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

  def * = (idBaskets, idUsers, idProducts) <> ((Basket.apply _).tupled, Basket.unapply)

  def idBaskets = column[Int]("idBaskets", O.PrimaryKey, O.AutoInc, O.Unique)

  def idProducts = column[Int]("idProducts")

  def idUsers = column[String]("idUsers")
}

//noinspection TypeAnnotation
object Basket {
  implicit val basketFormat = Json.format[Basket]
}