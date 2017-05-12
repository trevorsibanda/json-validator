package models

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ListProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}

import scala.util.{Either, Left, Right}

case class Document(val node: JsonNode)
object Document{
	def apply(s: String): Document = new Document(JsonLoader.fromString(s))
}

final case class Schema(val node: JsonNode){
         val jschema: JsonSchema = Schema.factory.getJsonSchema(node)

	def validate(n: JsonNode) = jschema.validate(n)
}
object Schema{
	val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault()

	def apply(schema: String): Schema = new Schema(JsonLoader.fromString(schema))

	def validate(schema: Schema): Boolean = try factory.getSyntaxValidator().validateSchema(schema.node).isSuccess catch{
		case e: Exception => throw e //wrap this
	}

	def validate(schema: String): Boolean = validate(apply(schema))
}

case class SchemaValidator(val schema: Schema){
	def validate(doc: Document): Either[ListProcessingReport, Document] = schema.validate(doc.node) match{
		case report: ListProcessingReport if report.isSuccess => Right(doc)
		case report: ListProcessingReport => Left(report)
	}
}
object SchemaValidator{
	def apply(s: String) = new SchemaValidator(Schema(s))
}
