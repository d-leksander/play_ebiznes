package models

import play.api.libs.json.Json

case class Basket(idBaskets: Int, idUsers: Int, idProducts: Int)

object Basket {
  implicit val basketFormat = Json.format[Basket]
}