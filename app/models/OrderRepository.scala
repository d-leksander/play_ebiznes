package models

import java.sql.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository, userRepository: UserRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import productRepository.ProductTable
  import userRepository.UserTable

  private val order = TableQuery[OrderTable]
  private val user = TableQuery[UserTable]
  private val product = TableQuery[ProductTable]


  private class OrderTable(tag: Tag) extends Table[Order](tag, "Orders") {
    def idOrders = column[Int]("idOrders", O.PrimaryKey, O.AutoInc, O.Unique)

    def idProducts = column[Int]("idProducts")

    def idUsers = column[Int]("idUsers")

    def date = column[Date]("date")

    def idUsers_fk = foreignKey("usr_fk", idUsers, user)(_.idUsers)

    def idProducts_fk = foreignKey("pro_fk", idProducts, product)(_.idProducts)

    def * = (idOrders, date, idUsers, idProducts) <> ((Order.apply _).tupled, Order.unapply)
  }

  def create(idUsers: Int, idProducts: Int, date: Date): Future[Order] = db.run {
    (order.map(o => (o.idUsers, o.idProducts, o.date))

      returning order.map(_.idOrders)
      into { case ((idUsers, idProducts, date), idOrders) => Order(idOrders, date, idUsers, idProducts) }
      ) += (idUsers, idProducts, date)
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def getByUser(user_id: Int): Future[Seq[Order]] = db.run {
    order.filter(_.idUsers === user_id).result
  }

  def getById(idOrders: Int): Future[Order] = db.run {
    order.filter(_.idOrders === idOrders).result.head
  }
}