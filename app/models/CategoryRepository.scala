package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val category = TableQuery[CategoryTable]

  class CategoryTable(tag: Tag) extends Table[Category](tag, "Categories") {
    def idCategories = column[Int]("idCategories", O.PrimaryKey, O.AutoInc, O.Unique)

    def name = column[String]("name")

    def * = (idCategories, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  def create(name: String): Future[Category] = db.run {
    (category.map(c => c.name)
      returning category.map(_.idCategories)
      into ((name, idCategories) => Category(idCategories, name))
      ) += name
  }

  def getById(idCategories: Int): Future[Category] = db.run {
    category.filter(_.idCategories === idCategories).result.head
  }

  def list(): Future[Seq[Category]] = db.run {
    category.result
  }
}