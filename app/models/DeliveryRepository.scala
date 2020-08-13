package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeliveryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val delivery = TableQuery[DeliveryTable]

  def create(company: String, price: Int): Future[Delivery] = db.run {
    (delivery.map(d => (d.company, d.price))
      returning delivery.map(_.idDelivery)
      into { case ((company, price), idDelivery) => Delivery(idDelivery, company, price) }
      ) += (company, price)
  }

  def getById(idDelivery: Int): Future[Delivery] = db.run {
    delivery.filter(_.idDelivery === idDelivery).result.head
  }

  def getByIdOption(id: Int): Future[Option[Delivery]] = db.run {
    delivery.filter(_.idDelivery === id).result.headOption
  }

  def list(): Future[Seq[Delivery]] = db.run {
    delivery.result
  }

  def delete(idDelivery: Int): Future[Unit] = db.run(delivery.filter(_.idDelivery === idDelivery).delete).map(_ => ())

  def update(id: Int, new_delivery: Delivery): Future[Unit] = {
    val deliveryToUpdate: Delivery = new_delivery.copy(id)
    db.run(delivery.filter(_.idDelivery === id).update(deliveryToUpdate)).map(_ => ())
  }
}
