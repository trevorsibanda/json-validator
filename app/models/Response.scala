package models

import play.api.libs.json._

abstract class Operation(val name: String)
object Operation{
	case object ValidateDocument extends Operation("validateDocument")
	case object SchemaUpload extends Operation("uploadSchema")
}

abstract class Status(val name: String)
object Status{
	case object Success extends Status("success")
	case object Error extends   Status("error")
}


trait Response{
	val id: String
	val action: Operation
	val status: Status
}
object Response{
	import Status._
	import Operation._
	
	abstract class SuccessResponse(val id: String, val action: Operation) extends Response{
		val status = Success
	}
	abstract class ErrorResponse(val id: String, val action: Operation, val message: String) extends Response{
		val status = Error
	}

	final case class ValidSchemaUpload(override val id: String) extends SuccessResponse(id, SchemaUpload)
	final case class ValidDocument(override val id: String) extends SuccessResponse(id, ValidateDocument)
	final case class InvalidSchemaUpload(override val id: String, override val message: String) extends ErrorResponse(id, SchemaUpload, message)
	final case class InvalidDocument(override val id: String, override val message: String) extends ErrorResponse(id, ValidateDocument, message)

	implicit val successResponseWrites = new Writes[SuccessResponse]{
		def writes(success: SuccessResponse) = Json.obj(
			"action" -> success.action.name,
			"id" -> success.id,
			"status" -> success.status.name
		)
	}

	implicit val errorResponseWrites = new Writes[ErrorResponse]{
		def writes(err: ErrorResponse) = Json.obj(
			"action" -> err.action.name,
			"id" -> err.id,
			"message" -> err.message,
			"status" -> err.status.name
		)
	}

}

