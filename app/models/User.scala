package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class User(idUsers: Int, password: String, email: String, idSettings: Int)

class UserTable(tag: Tag) extends Table[User](tag, "Users") {
  val settings = TableQuery[SettingsTable]

  def idSettings_fk = foreignKey("set_fk", idSettings, settings)(_.idSettings)

  def * = (idUsers, password, email, idSettings) <> ((User.apply _).tupled, User.unapply)

  def idUsers = column[Int]("idUsers", O.PrimaryKey, O.AutoInc, O.Unique)

  def password = column[String]("password")

  def email = column[String]("email")

  def idSettings = column[Int]("idSettings")
}

object User {
  implicit val userFormat = Json.format[User]
}