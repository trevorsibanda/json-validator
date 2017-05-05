package controllers

import play.api._
import play.api.mvc._
import models._
import models.Response._
import play.api.libs.json._

class SchemaController extends Controller{
	def create(id: String) = Action{request =>
		val success = ValidSchemaUpload(id)
		Status(201)(Json.toJson(success))
	}

	def download(id: String) = Action{request =>
		Ok("{}")
	}

	def validate(id: String) = Action{request =>
		val success = ValidDocument(id)
		Ok(Json.toJson(success))
	}
}

