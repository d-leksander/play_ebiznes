package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val user = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "Users") {
    def idUsers = column[Int]("idUsers", O.PrimaryKey, O.AutoInc, O.Unique)

    def password = column[String]("password")

    def email = column[String]("email")

    def * = (idUsers, password, email) <> ((User.apply _).tupled, User.unapply)
  }

  def create(password: String, email: String): Future[User] = db.run {
    (user.map(u => (u.password, u.email))

      returning user.map(_.idUsers)
      into { case ((password, email), idUsers) => User(idUsers, password, email) }
      ) += (password, email)
  }

  def list(): Future[Seq[User]] = db.run {
    user.result
  }

  def getById(idUsers: Int): Future[User] = db.run {
    user.filter(_.idUsers === idUsers).result.head
  }

  def delete(idUsers: Int): Future[Unit] = db.run(user.filter(_.idUsers === idUsers).delete).map(_ => ())

  def update(idUsers: Int, new_user: User): Future[Unit] = {
    val userToUpdate: User = new_user.copy(idUsers)
    db.run(user.filter(_.idUsers === idUsers).update(userToUpdate)).map(_ => ())
  }
}