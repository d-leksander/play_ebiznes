package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Category(idCategories: Int, name: String)

class CategoryTable(tag: Tag) extends Table[Category](tag, "Categories") {
  def * = (idCategories, name) <> ((Category.apply _).tupled, Category.unapply)

  def idCategories = column[Int]("idCategories", O.PrimaryKey, O.AutoInc, O.Unique)

  def name = column[String]("name")
}

object Category {
  implicit val categoryFormat = Json.format[Category]
}