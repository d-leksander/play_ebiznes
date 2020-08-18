package models

import java.sql.Date

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Order(idOrders: Int, date: Date, idUsers: String, idProducts: Int)

class OrderTable(tag: Tag) extends Table[Order](tag, "Orders") {
  val user = TableQuery[UserTable]
  val product = TableQuery[ProductTable]

  def idUsersFk = foreignKey("usr_fk", idUsers, user)(_.idU)

  def idUsers = column[String]("idUsers")

  def idProductsFk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

  def idProducts = column[Int]("idProducts")

  def * = (idOrders, date, idUsers, idProducts) <> ((Order.apply _).tupled, Order.unapply)

  def idOrders = column[Int]("idOrders", O.PrimaryKey, O.AutoInc, O.Unique)

  def date = column[Date]("date")
}

object Order {
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}