package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Photo(idPhotos: Int, path: String)

class PhotoTable(tag: Tag) extends Table[Photo](tag, "Photos") {
  def * = (idPhotos, path) <> ((Photo.apply _).tupled, Photo.unapply)

  def idPhotos = column[Int]("idPhotos", O.PrimaryKey, O.AutoInc, O.Unique)

  def path = column[String]("path")
}

object Photo {
  implicit val photoFormat = Json.format[Photo]
}