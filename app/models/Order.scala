package models

import java.sql.Date
import play.api.libs.json.Json

case class Order(idOrders: Int, date: Date, idUsers: Int, idProducts: Int)

object Order {
  implicit val orderFormat = Json.format[Order]
}