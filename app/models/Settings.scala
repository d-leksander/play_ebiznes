package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Settings(idSettings: Int, description: String, age: Int)

class SettingsTable(tag: Tag) extends Table[Settings](tag, "Settings") {
  def * = (idSettings, description, age) <> ((Settings.apply _).tupled, Settings.unapply)

  def idSettings = column[Int]("idSettings", O.PrimaryKey, O.AutoInc, O.Unique)

  def description = column[String]("description")

  def age = column[Int]("age")
}

object Settings {
  implicit val settingsFormat = Json.format[Settings]
}