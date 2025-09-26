package uk.gov.hmrc.stubs

import uk.gov.hmrc.stubs.models.BusinessEntity
import play.api.libs.json.JsValue
import scala.concurrent.Future
import java.util.UUID

/** Pure stub implementation for business entity registration API.
  *
  * This stub provides pre-determined responses based on JSON content patterns.
  *
  * TODO: (24/09) MA - Make a card to remove this file when the actual API implementation is built and implemented.
  */

case class ApiResponse(statusCode: Int, body: String)

object BusinessEntityApiStub {

  private val basePath = "/senior-accounting-officer-hod/business-entity"

  def call(
    method: String,
    url: String,
    body: Option[JsValue] = None,
    contentType: String = "application/json"
  ): Future[ApiResponse] =
    (method.toUpperCase, url) match {
      case ("PUT", basePath) =>
        validateAndCallPutRequest(body, contentType)

      case ("GET", path) if path.startsWith(s"$basePath/") =>
        val id = path.replace(s"$basePath/", "")
        validateAndCallGetRequest(id)
    }

  private def validateAndCallPutRequest(requestBody: Option[JsValue], contentType: String): Future[ApiResponse] = {
    if (contentType != "application/json") {
      return badRequest(
        "Content-Type must be application/json"
      ) // Future.successful(ApiResponse(400, """{"error":"Content-Type must be application/json"}"""))
    }

    requestBody match {
      case None =>
        badRequest(
          "Request body is required"
        ) // Future.successful(ApiResponse(400, """{"error":"Request body is required"}"""))

      case Some(body) =>
        // Parse business entity from JSON body
        body
          .validate[BusinessEntity]
          .fold(
            errors =>
              badRequest(
                "Invalid JSON format"
              ), // Future.successful(ApiResponse(400, """{"error":"Invalid JSON format"}""")),
            businessEntity => processEntity(businessEntity)
          )
    }
  }

  private def processEntity(businessEntity: BusinessEntity): Future[ApiResponse] =
    businessEntity.name match {
      case "" => badRequest("Invalid business entity data")
      // Future.successful(ApiResponse(400, """{"error":"Invalid business entity data"}"""))

      case "DuplicateCompany" =>
        Future.successful(
          ApiResponse(409, errorMessage("Business entity already exists"))
        ) // """{"error":"Business entity already exists"}"""

      case _ =>
        Future.successful(
          ApiResponse(200, s"""{"message":"Business entity updated successfully","id":"${businessEntity.id}"}""")
        )
    }

  private def validateAndCallGetRequest(id: String): Future[ApiResponse] = {
    // Validate UUID format
    try
      UUID.fromString(id)
    catch {
      case _: IllegalArgumentException =>
        return badRequest(
          "Invalid UUID format"
        ) // Future.successful(ApiResponse(400, """{"error":"Invalid UUID format"}"""))
    }

    // Handle different scenarios based on ID
    id match {
      case "00000000-0000-0000-0000-000000000000" => // Non-existent ID
        Future.successful(
          ApiResponse(404, errorMessage("Business entity not found"))
        ) // """{"error":"Business entity not found"}"""

      case _ =>
        val stubEntity = s"""{"id":"$id","name":"Test Company Ltd","crn":"12345678","utr":"1234567890"}"""
        Future.successful(ApiResponse(200, stubEntity))
    }
  }

  private def badRequest(message: String): Future[ApiResponse] =
    Future.successful(ApiResponse(400, errorMessage(message)))

  private def errorMessage(message: String): String = s"""{"error":"$message"}"""
}
