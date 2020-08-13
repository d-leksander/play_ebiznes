package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Delivery(idDelivery: Int, company: String, price: Int)

class DeliveryTable(tag: Tag) extends Table[Delivery](tag, "Delivery") {
  def * = (idDelivery, company, price) <> ((Delivery.apply _).tupled, Delivery.unapply)

  def idDelivery = column[Int]("idDelivery", O.PrimaryKey, O.AutoInc, O.Unique)

  def company = column[String]("type")

  def price = column[Int]("price")
}

object Delivery {
  implicit val deliveryFormat = Json.format[Delivery]
}
