package dao

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.JdbcProfile
import java.sql.Timestamp

object SchemaDAO{
	case class SchemaRow(val id: String, val json: String, val created: Timestamp)
}

@Singleton class SchemaDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit exceutionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{
	import dbConfig.profile.api._
	import SchemaDAO._

	class SchemaTable(_tag: Tag) extends Table[SchemaRow](_tag, "schema"){
		def id: Rep[String] = column[String]("id", O.PrimaryKey)
		def json: Rep[String] = column[String]("json")
		def created: Rep[Timestamp] = column[Timestamp]("created")

		def * = (id, json, created)<>(SchemaRow.tupled, SchemaRow.unapply)
	}

	private val schemaTable = TableQuery[SchemaTable]

	def get(id: String) ={
		db.run(schemaTable.filter(_.id === id ).result.headOption)
	}

	def schema_create = {
		schemaTable.schema.create.statements
	}

	def insert(srow: SchemaRow) ={
		db.run(schemaTable ++= Seq(srow))
	}

	def exists(id: String) = {
		db.run(schemaTable.filter(_.id === id).exists.result)
	}
}

