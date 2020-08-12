package models

import play.api.libs.json.Json

case class User(idUsers: Int, password: String, email: String)

object User {
  implicit val userFormat = Json.format[User]
}