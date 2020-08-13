package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, settingsRepository: SettingsRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val user = TableQuery[UserTable]
  private val settings = TableQuery[SettingsTable]

  def create(password: String, email: String, idSettings: Int): Future[User] = db.run {
    (user.map(u => (u.password, u.email, u.idSettings))

      returning user.map(_.idUsers)
      into { case ((password, email, idSettings), idUsers) => User(idUsers, password, email, idSettings) }
      ) += (password, email, idSettings)
  }

  def list(): Future[Seq[User]] = db.run {
    user.result
  }

  def getById(idUsers: Int): Future[User] = db.run {
    user.filter(_.idUsers === idUsers).result.head
  }

  def getByIdOption(id: Int): Future[Option[User]] = db.run {
    user.filter(_.idUsers === id).result.headOption
  }

  def getSettings(user: User): Future[Settings] = db.run {
    settings.filter(_.idSettings === user.idSettings).result.head
  }

  def delete(idUsers: Int): Future[Unit] = db.run(user.filter(_.idUsers === idUsers).delete).map(_ => ())

  def update(idUsers: Int, new_user: User): Future[Unit] = {
    val userToUpdate: User = new_user.copy(idUsers)
    db.run(user.filter(_.idUsers === idUsers).update(userToUpdate)).map(_ => ())
  }
}