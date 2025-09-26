/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.stubs

import uk.gov.hmrc.stubs.models.BusinessEntity
import play.api.libs.json.{JsValue, Json}
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
    if (contentType != "application/json") return badRequest("Content-Type must be application/json")

    requestBody match {
      case None => badRequest("Request body is required")

      case Some(body) =>
        // Parse business entity from JSON body
        body
          .validate[BusinessEntity]
          .fold(
            errors => badRequest("Invalid JSON format"),
            businessEntity => processEntity(businessEntity)
          )
    }
  }

  private def processEntity(businessEntity: BusinessEntity): Future[ApiResponse] =
    businessEntity.name match {
      case "" => badRequest("Invalid business entity data")

      case "DuplicateCompany" =>
        Future.successful(
          ApiResponse(409, errorMessage("Business entity already exists"))
        )

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
      case _: IllegalArgumentException => return badRequest("Invalid UUID format")
    }

    // Handle different scenarios based on ID
    id match {
      case "00000000-0000-0000-0000-000000000000" => // Non-existent ID
        Future.successful(ApiResponse(404, errorMessage("Business entity not found")))

      case _ =>
        val stubEntity = TestDataFactory.validBusinessEntity().copy(id = UUID.fromString(id))
        Future.successful(ApiResponse(200, Json.toJson(stubEntity).toString()))
    }
  }

  private def badRequest(message: String): Future[ApiResponse] =
    Future.successful(ApiResponse(400, errorMessage(message)))

  private def errorMessage(message: String): String = s"""{"error":"$message"}"""
}
