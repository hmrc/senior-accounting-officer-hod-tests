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

import uk.gov.hmrc.stubs.models.{BusinessEntity, Certificate}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future
import java.util.UUID
import scala.util.{Failure, Success, Try}

/** Pure stub implementation for business entity registration API.
  *
  * This stub provides pre-determined responses based on JSON content patterns.
  *
  * TODO: (24/09) MA - Make a card to remove this file when the actual API implementation is built and implemented.
  */

case class ApiResponse(statusCode: Int, body: String)

object ApiStubs {

  private val baseUrl           = "/senior-accounting-officer-hod"
  private val registrationPath  = s"$baseUrl/business-entity"
  private val certificationPath = s"$baseUrl/certification"

  def call(
    method: String,
    url: String,
    body: Option[JsValue] = None,
    contentType: String = "application/json"
  ): Future[ApiResponse] =
    (method.toUpperCase, url) match {
      case ("PUT", url) if url.equalsIgnoreCase(registrationPath) => validateAndCallRegister(body, contentType)

      case ("PUT", url) if url.equalsIgnoreCase(certificationPath) => validateAndCallCertify(body)

      case ("GET", url) if url.startsWith(registrationPath) =>
        val id = url.replace(s"$registrationPath/", "")
        validateAndCallGetRequest(id)
    }

  private def validateAndCallRegister(requestBody: Option[JsValue], contentType: String): Future[ApiResponse] = {
    if (contentType != "application/json") return badRequest("Content-Type must be application/json")

    requestBody match {
      case None => badRequest("Request body is required")

      case Some(body) =>
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

  private def validateAndCallCertify(requestBody: Option[JsValue]): Future[ApiResponse] =
    requestBody match {
      case Some(body) =>
        body
          .validate[Certificate]
          .fold(
            errors => badRequest("Invalid JSON format"),
            certificate => processCertificate(certificate)
          )
    }

  private def processCertificate(certificate: Certificate): Future[ApiResponse] =
    certificate match {
      case _ =>
        val certificationId: String = s"SAOCERT${TestDataFactory.randomAlphanumericId(8)}"
        val responseBody            = s"""{"message":"Certification complete","certificateSafeId":"$certificationId"}"""
        val response                = ApiResponse(200, responseBody)

        Future.successful(response)
    }

  private def validateAndCallGetRequest(id: String): Future[ApiResponse] =
    Try(UUID.fromString(id)) match {
      case Failure(_) =>
        badRequest("Invalid UUID format")

      case Success(_) if id == "00000000-0000-0000-0000-000000000000" =>
        Future.successful(ApiResponse(404, errorMessage("Business entity not found")))

      case Success(uuid) =>
        val stubEntity = TestDataFactory.validBusinessEntity().copy(id = uuid)
        Future.successful(ApiResponse(200, Json.toJson(stubEntity).toString()))
    }

  private def badRequest(message: String): Future[ApiResponse] =
    Future.successful(ApiResponse(400, errorMessage(message)))

  private def errorMessage(message: String): String = s"""{"error":"$message"}"""

}
