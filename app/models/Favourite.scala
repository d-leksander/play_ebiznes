package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Favourite(idFavourites: Int, idUsers: String, idProducts: Int)

class FavouriteTable(tag: Tag) extends Table[Favourite](tag, "Favourites") {
  val user = TableQuery[UserTable]
  val product = TableQuery[ProductTable]

  def idUsersFk = foreignKey("usr_fk", idUsers, user)(_.id)

  def idUsers = column[String]("idUsers")

  def idProductsFk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

  def idProducts = column[Int]("idProducts")

  def * = (idFavourites, idUsers, idProducts) <> ((Favourite.apply _).tupled, Favourite.unapply)

  def idFavourites = column[Int]("idFavourites", O.PrimaryKey, O.AutoInc, O.Unique)
}

object Favourite {
  implicit val favouriteFormat: OFormat[Favourite] = Json.format[Favourite]
}