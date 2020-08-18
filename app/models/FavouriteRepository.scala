package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FavouriteRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository, userRepository: UserRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val favourite = TableQuery[FavouriteTable]

  def create(idUsers: String, idProducts: Int): Future[Favourite] = db.run {
    (favourite.map(f => (f.idUsers, f.idProducts))

      returning favourite.map(_.idFavourites)
      into { case ((idUsers, idProducts), idFavourites) => Favourite(idFavourites, idUsers, idProducts) }
      ) += (idUsers, idProducts)
  }

  def list(): Future[Seq[Favourite]] = db.run {
    favourite.result
  }

  def getByUser(userId: String): Future[Seq[Favourite]] = db.run {
    favourite.filter(_.idUsers === userId).result
  }

  def getById(idFavourites: Int): Future[Favourite] = db.run {
    favourite.filter(_.idFavourites === idFavourites).result.head
  }

  def getByIdOption(id: Int): Future[Option[Favourite]] = db.run {
    favourite.filter(_.idFavourites === id).result.headOption
  }

  def delete(idFavourites: Int): Future[Unit] = db.run(favourite.filter(_.idFavourites === idFavourites).delete).map(_ => ())

  def update(id: Int, newFavourite: Favourite): Future[Unit] = {
    val favouriteToUpdate: Favourite = newFavourite.copy(id)
    db.run(favourite.filter(_.idFavourites === id).update(favouriteToUpdate)).map(_ => ())
  }
}