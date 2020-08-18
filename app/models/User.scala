package models

import play.api.libs.json.Json
import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class User(id: String, firstName: String, lastName: String, email: String, roleId: Int, avatarUrl: String)

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[String]("id", O.PrimaryKey)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def email = column[String]("email")
  def roleId = column[Int]("roleId")
  def avatarUrl = column[String]("avatar_url")
  def * = (id, firstName, lastName, email, roleId, avatarUrl) <> ((User.apply _).tupled, User.unapply)
}

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}