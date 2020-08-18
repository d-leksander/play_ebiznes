package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BasketRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository, userRepository: UserRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val basket = TableQuery[BasketTable]

  def create(idUsers: String, idProducts: Int): Future[Basket] = db.run {
    (basket.map(b => (b.idUsers, b.idProducts))

      returning basket.map(_.idBaskets)
      into { case ((idUsers, idProducts), idBaskets) => Basket(idBaskets, idUsers, idProducts) }
      ) += (idUsers, idProducts)
  }

  def list(): Future[Seq[Basket]] = db.run {
    basket.result
  }

  def getByUser(userId: String): Future[Seq[Basket]] = db.run {
    basket.filter(_.idUsers === userId).result
  }

  def getById(idBaskets: Int): Future[Basket] = db.run {
    basket.filter(_.idBaskets === idBaskets).result.head
  }

  def getByIdOption(id: Int): Future[Option[Basket]] = db.run {
    basket.filter(_.idBaskets === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run(basket.filter(_.idBaskets === id).delete).map(_ => ())

  def update(id: Int, newBasket: Basket): Future[Unit] = {
    val basketToUpdate: Basket = newBasket.copy(id)
    db.run(basket.filter(_.idBaskets === id).update(basketToUpdate)).map(_ => ())
  }
}