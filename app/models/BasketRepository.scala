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
  import productRepository.ProductTable
  import userRepository.UserTable

  private val basket = TableQuery[BasketTable]
  private val user = TableQuery[UserTable]
  private val product = TableQuery[ProductTable]


  private class BasketTable(tag: Tag) extends Table[Basket](tag, "Baskets") {
    def idBaskets = column[Int]("idBaskets", O.PrimaryKey, O.AutoInc, O.Unique)

    def idProducts = column[Int]("idProducts")

    def idUsers = column[Int]("idUsers")

    def idUsers_fk = foreignKey("usr_fk", idUsers, user)(_.idUsers)

    def idProducts_fk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

    def * = (idBaskets, idUsers, idProducts) <> ((Basket.apply _).tupled, Basket.unapply)
  }

  def create(idUsers: Int, idProducts: Int): Future[Basket] = db.run {
    (basket.map(b => (b.idUsers, b.idProducts))

      returning basket.map(_.idBaskets)
      into { case ((idUsers, idProducts), idBaskets) => Basket(idBaskets, idUsers, idProducts) }
      ) += (idUsers, idProducts)
  }

  def list(): Future[Seq[Basket]] = db.run {
    basket.result
  }

  def getByUser(user_id: Int): Future[Seq[Basket]] = db.run {
    basket.filter(_.idUsers === user_id).result
  }

  def getById(idBaskets: Int): Future[Basket] = db.run {
    basket.filter(_.idBaskets === idBaskets).result.head
  }

  def delete(idBaskets: Int): Future[Unit] = db.run(basket.filter(_.idBaskets === idBaskets).delete).map(_ => ())

  def update(id: Int, new_basket: Basket): Future[Unit] = {
    val basketToUpdate: Basket = new_basket.copy(id)
    db.run(basket.filter(_.idBaskets === id).update(basketToUpdate)).map(_ => ())
  }
}