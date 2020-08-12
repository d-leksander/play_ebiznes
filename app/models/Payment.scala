package models

import java.sql.Date
import play.api.libs.json.Json

case class Payment(idPayments: Int, status: String, date: Date, idUsers: Int, value: Int)

object Payment {
  implicit val paymentFormat = Json.format[Payment]
}