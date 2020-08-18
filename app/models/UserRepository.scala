package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val user = TableQuery[UserTable]

  def list(): Future[Seq[User]] = db.run {
    user.result
  }

  def getById(id: String): Future[User] = db.run {
    user.filter(_.id === id).result.head
  }

  def getByIdOption(id: String): Future[Option[User]] = db.run {
    user.filter(_.id === id).result.headOption
  }
}