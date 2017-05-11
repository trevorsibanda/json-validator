package controllers

import play.api._
import play.api.mvc._
import models._
import dao._
import models.Response._
import play.api.libs.json._
import javax.inject.Inject
import scala.concurrent.ExecutionContext

object ServerError{
	abstract class ServerError(val message: String)
	case class SchemaNotFound(val id: String) extends ServerError(s"Schema for id `$id` was not found")
	case class SchemaIDExists(val id: String) extends ServerError(s"Schema with id `$id` already exists")
	case class PersistanceFailure(val id: String) extends ServerError(s"Failed to save schema `$id`, database IO failure")
	case class UnhandledFailure(val msg: String) extends ServerError("An unknown error occured. If this persists please contact support"){
		println(s"[UNHANDLED_FAILURE] $msg")
	}

	implicit val serverErrorWrites = new Writes[ServerError]{
		def writes(se: ServerError) = Json.obj("status" -> "error", "msg" -> se.message)
	}
}

class SchemaController @Inject()(schemaDAO: SchemaDAO)(implicit executionContext: ExecutionContext)  extends Controller{
	import SchemaDAO._
	import ServerError._
	import scala.util.{Success, Failure}
	import scala.concurrent.Future

	def create(id: String) = Action.async(parse.tolerantText){implicit request =>
		schemaDAO.get(id) map{
			case Some(_) => Status(403)(Json.toJson(SchemaIDExists(id)))
			case None => doCreate(id, request.body)
		}
	}

	def download(id: String) = Action.async{request =>
		schemaDAO.get(id) map{
			case Some(row) => Ok(Json.parse(row.json))
			case None => Status(404)(Json.toJson(SchemaNotFound(id)))
		}
	}

	def validate(id: String) = Action.async(parse.tolerantText){request =>
		schemaDAO.get(id) map{
			case Some(row) => doValidate(row, request.body)
			case None => Status(404)(Json.toJson(SchemaNotFound(id)))
		}
	}

	private def doCreate(id: String, json: String) = {
		val now = new java.sql.Timestamp(java.util.Calendar.getInstance().getTimeInMillis)
		val row = SchemaRow(id, json, now)
		val schema = Schema(row.json)
		val invalidSchema = Status(500)(Json.toJson(InvalidSchemaUpload(id, "Schema is not a valid schema")))
		try{
			Schema.validate(schema) match{
				case true => {
					schemaDAO.insert(row)
					Status(201)(Json.toJson(ValidSchemaUpload(id)))
				}
				case false => invalidSchema
			}
		}catch{
			case e: Exception => invalidSchema
		}
	}

	private def doValidate(row: SchemaRow, json: String) = {
		import scala.util.{Either, Right, Left}
		try{
			val validator = SchemaValidator(row.json)
			val doc = Document(json)
			validator.validate(doc) match{
				case Right(doc) => Ok(Json.toJson(ValidDocument(row.id)))
				case Left(report) => Status(201)(Json.toJson(InvalidDocument(row.id, "Invalid document")))
			}
		}catch{
			case e: Exception => Status(500)(Json.toJson(UnhandledFailure(e.getMessage())))
		}
	}

}

