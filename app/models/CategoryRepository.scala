package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val category = TableQuery[CategoryTable]

  def create(name: String): Future[Category] = db.run {
    (category.map(c => c.name)
      returning category.map(_.idCategories)
      into { case (name, idCategories) => Category(idCategories, name) }
      ) += name
  }

  def getById(idCategories: Int): Future[Category] = db.run {
    category.filter(_.idCategories === idCategories).result.head
  }

  def getByIdOption(id: Int): Future[Option[Category]] = db.run {
    category.filter(_.idCategories === id).result.headOption
  }

  def list(): Future[Seq[Category]] = db.run {
    category.result
  }
}