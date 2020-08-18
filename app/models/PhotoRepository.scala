package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PhotoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val photo = TableQuery[PhotoTable]

  def create(path: String): Future[Photo] = db.run {
    (photo.map(p => p.path)
      returning photo.map(_.idPhotos)
      into { case (path, idPhotos) => Photo(idPhotos, path) }
      ) += path
  }

  def getById(idPhotos: Int): Future[Photo] = db.run {
    photo.filter(_.idPhotos === idPhotos).result.head
  }

  def getByIdOption(id: Int): Future[Option[Photo]] = db.run {
    photo.filter(_.idPhotos === id).result.headOption
  }

  def list(): Future[Seq[Photo]] = db.run {
    photo.result
  }

  def delete(idPhotos: Int): Future[Unit] = db.run(photo.filter(_.idPhotos === idPhotos).delete).map(_ => ())

  def update(id: Int, newPhoto: Photo): Future[Unit] = {
    val photoToUpdate: Photo = newPhoto.copy(id)
    db.run(photo.filter(_.idPhotos === id).update(photoToUpdate)).map(_ => ())
  }
}
