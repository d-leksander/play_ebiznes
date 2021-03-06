package models

import java.sql.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val order = TableQuery[OrderTable]

  def create(date: Date, idUsers: String, idProducts: Int): Future[Order] = db.run {
    (order.map(o => (o.date, o.idUsers, o.idProducts))

      returning order.map(_.idOrders)
      into { case ((date, idUsers, idProducts), idOrders) => Order(idOrders, date, idUsers, idProducts) }
      ) += (date, idUsers, idProducts)
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def getByUser(userId: String): Future[Seq[Order]] = db.run {
    order.filter(_.idUsers === userId).result
  }

  def getById(idOrders: Int): Future[Order] = db.run {
    order.filter(_.idOrders === idOrders).result.head
  }

  def getByIdOption(id: Int): Future[Option[Order]] = db.run {
    order.filter(_.idOrders === id).result.headOption
  }

  def update(id: Int, newOrder: Order): Future[Unit] = {
    val orderToUpdate: Order = newOrder.copy(id)
    db.run(order.filter(_.idOrders === id).update(orderToUpdate)).map(_ => ())
  }
}