package models

import play.api.libs.json.Json

case class Product(idProducts: Int, name: String, description: String, idCategories: Int, price: Int)

object Product {
  implicit val productFormat = Json.format[Product]
}