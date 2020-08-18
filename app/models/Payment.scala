package models

import java.sql.Date

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.SQLiteProfile.api._

case class Payment(idPayments: Int, status: String, date: Date, idUsers: String, value: Int)

class PaymentTable(tag: Tag) extends Table[Payment](tag, "Payments") {
  val users = TableQuery[UserTable]

  def categoryFk = foreignKey("usr_fk", idUsers, users)(_.id)

  def idUsers = column[String]("idUsers")

  def * = (idPayments, status, date, idUsers, value) <> ((Payment.apply _).tupled, Payment.unapply)

  def idPayments = column[Int]("idPayments", O.PrimaryKey, O.AutoInc, O.Unique)

  def status = column[String]("status")

  def date = column[Date]("date")

  def value = column[Int]("value")
}

object Payment {
  implicit val paymentFormat: OFormat[Payment] = Json.format[Payment]
}