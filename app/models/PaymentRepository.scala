package models

import java.sql.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import userRepository.UserTable

  private val payment = TableQuery[PaymentTable]
  private val users = TableQuery[UserTable]

  private class PaymentTable(tag: Tag) extends Table[Payment](tag, "Payments") {
    def idPayments = column[Int]("idPayments", O.PrimaryKey, O.AutoInc, O.Unique)

    def status = column[String]("status")

    def date = column[Date]("date")

    def idUsers = column[Int]("idUsers")

    def value = column[Int]("value")

    def category_fk = foreignKey("usr_fk", idUsers, users)(_.idUsers)

    def * = (idPayments, status, date, idUsers, value) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  def create(status: String, date: Date, idUsers: Int, value: Int): Future[Payment] = db.run {
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

  def getByUser(user_id: Int): Future[Seq[Payment]] = db.run {
    payment.filter(_.idUsers === user_id).result
  }

  def updateStatus(idPayments: Int, new_payment: Payment): Future[Unit] = {
    val paymentToUpdate: Payment = new_payment.copy(idPayments)
    db.run(payment.filter(_.idPayments === idPayments).update(paymentToUpdate)).map(_ => ())
  }
}