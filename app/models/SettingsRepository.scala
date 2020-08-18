package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SettingsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val settings = TableQuery[SettingsTable]

  def create(description: String, age: Int): Future[Settings] = db.run {
    (settings.map(s => (s.description, s.age))
      returning settings.map(_.idSettings)
      into { case ((description, age), idSettings) => Settings(idSettings, description, age) }
      ) += (description, age)
  }

  def getById(idSettings: Int): Future[Settings] = db.run {
    settings.filter(_.idSettings === idSettings).result.head
  }

  def getByIdOption(idSettings: Int): Future[Option[Settings]] = db.run {
    settings.filter(_.idSettings === idSettings).result.headOption
  }

  def list(): Future[Seq[Settings]] = db.run {
    settings.result
  }

  def update(id: Int, newSettings: Settings): Future[Unit] = {
    val settingsToUpdate: Settings = newSettings.copy(id)
    db.run(settings.filter(_.idSettings === id).update(settingsToUpdate)).map(_ => ())
  }
}
