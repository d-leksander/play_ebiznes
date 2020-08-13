package models

import java.sql.Date

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Order(idOrders: Int, date: Date, idUsers: Int, idProducts: Int)

class OrderTable(tag: Tag) extends Table[Order](tag, "Orders") {
  val user = TableQuery[UserTable]
  val product = TableQuery[ProductTable]

  def idUsers_fk = foreignKey("usr_fk", idUsers, user)(_.idUsers)

  def idUsers = column[Int]("idUsers")

  def idProducts_fk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

  def idProducts = column[Int]("idProducts")

  def * = (idOrders, date, idUsers, idProducts) <> ((Order.apply _).tupled, Order.unapply)

  def idOrders = column[Int]("idOrders", O.PrimaryKey, O.AutoInc, O.Unique)

  def date = column[Date]("date")
}

object Order {
  implicit val orderFormat = Json.format[Order]
}