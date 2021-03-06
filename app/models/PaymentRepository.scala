package models

import java.sql.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, userRepository: UserRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val payment = TableQuery[PaymentTable]

  def create(status: String, date: Date, idUsers: String, value: Int): Future[Payment] = db.run {
    (payment.map(p => (p.status, p.date, p.idUsers, p.value))

      returning payment.map(_.idPayments)
      into { case ((status, date, idUsers, value), idPayments) => Payment(idPayments, status, date, idUsers, value) }
      ) += (status, date, idUsers, value)
  }

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def getById(idPayments: Int): Future[Payment] = db.run {
    payment.filter(_.idPayments === idPayments).result.head
  }

  def getByIdOption(id: Int): Future[Option[Payment]] = db.run {
    payment.filter(_.idPayments === id).result.headOption
  }

  def getByUser(userId: String): Future[Seq[Payment]] = db.run {
    payment.filter(_.idUsers === userId).result
  }

  def update(idPayments: Int, newPayment: Payment): Future[Unit] = {
    val paymentToUpdate: Payment = newPayment.copy(idPayments)
    db.run(payment.filter(_.idPayments === idPayments).update(paymentToUpdate)).map(_ => ())
  }
}