package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Favourite(idFavourites: Int, idUsers: Int, idProducts: Int)

class FavouriteTable(tag: Tag) extends Table[Favourite](tag, "Favourites") {
  val user = TableQuery[UserTable]
  val product = TableQuery[ProductTable]

  def idUsers_fk = foreignKey("usr_fk", idUsers, user)(_.idUsers)

  def idUsers = column[Int]("idUsers")

  def idProducts_fk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

  def idProducts = column[Int]("idProducts")

  def * = (idFavourites, idUsers, idProducts) <> ((Favourite.apply _).tupled, Favourite.unapply)

  def idFavourites = column[Int]("idFavourites", O.PrimaryKey, O.AutoInc, O.Unique)
}

object Favourite {
  implicit val favouriteFormat = Json.format[Favourite]
}