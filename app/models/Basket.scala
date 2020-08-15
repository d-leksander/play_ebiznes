package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Basket(idBaskets: Int, idUsers: Int, idProducts: Int)

class BasketTable(tag: Tag) extends Table[Basket](tag, "Baskets") {
  val user = TableQuery[UserTable]
  val product = TableQuery[ProductTable]

  def idUsers_fk = foreignKey("usr_fk", idUsers, user)(_.idUsers)

  def idProducts_fk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

  def * = (idBaskets, idUsers, idProducts) <> ((Basket.apply _).tupled, Basket.unapply)

  def idBaskets = column[Int]("idBaskets", O.PrimaryKey, O.AutoInc, O.Unique)

  def idProducts = column[Int]("idProducts")

  def idUsers = column[Int]("idUsers")
}

//noinspection TypeAnnotation
object Basket {
  implicit val basketFormat = Json.format[Basket]
}