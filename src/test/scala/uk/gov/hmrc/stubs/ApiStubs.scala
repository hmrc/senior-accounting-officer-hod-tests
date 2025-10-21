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

import uk.gov.hmrc.stubs.models.{BusinessEntity, Certificate, Notification}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.adt._
import uk.gov.hmrc.adt.ResponseType._

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
  private val notificationPath  = s"$baseUrl/notification"

  def call(
    method: String,
    url: String,
    body: Option[JsValue] = None,
    contentType: String = "application/json",
    basicAuth: String = "Administrator"
  ): Future[ApiResponse] =
    (method.toUpperCase, url) match {
      case ("PUT", url) if url.equalsIgnoreCase(registrationPath) => validateAndCallRegister(body, contentType)

      case ("PUT", url) if url.equalsIgnoreCase(certificationPath) => validateAndCallCertify(body, basicAuth)

      case ("PUT", url) if url.equalsIgnoreCase(notificationPath) => validateAndCallNotify(body, contentType)

      case ("GET", url) if url.startsWith(registrationPath) =>
        val id = url.replace(s"$registrationPath/", "")
        validateAndCallGetRequest(id)
    }

  private def validateAndCallRegister(requestBody: Option[JsValue], contentType: String): Future[ApiResponse] =
    if (!contentType.equalsIgnoreCase("application/json")) {
      responseState(BadRequest("Content-Type must be application/json"))
    } else {
      requestBody match {
        case None => responseState(BadRequest("Request body is required"))

        case Some(body) =>
          body
            .validate[BusinessEntity]
            .fold(
              errors => responseState(BadRequest("Invalid JSON format")),
              businessEntity => processEntity(businessEntity)
            )
      }
    }

  private def processEntity(businessEntity: BusinessEntity): Future[ApiResponse] =
    businessEntity.name match {
      case "" => responseState(BadRequest("Invalid business entity data"))

      case "DuplicateCompany" =>
        responseState(Conflict("Business entity already exists"))

      case _ =>
        responseState(BusinessEntityUpdated(businessEntity.id.toString))
    }

  private def validateAndCallCertify(requestBody: Option[JsValue], role: String): Future[ApiResponse] =
    if (!role.equalsIgnoreCase("Administrator")) {
      responseState(Unauthorized)
    } else {
      requestBody match {
        case None => responseState(BadRequest("Request body is required"))

        case Some(body) =>
          body
            .validate[Certificate]
            .fold(
              errors => responseState(BadRequest("Invalid JSON format")),
              certificate => processCertificate(certificate)
            )
      }
    }

  private def processCertificate(certificate: Certificate): Future[ApiResponse] =
    certificate.certificateId match {
      case Some(existingId) =>
        val certificationId = s"SAOCERT$existingId"
        responseState(CertificateUpdated(certificationId))

      case None =>
        val certificationId = s"SAOCERT${TestDataFactory.randomAlphanumericId(8)}"
        responseState(CertificationComplete(certificationId))
    }

  private def validateAndCallNotify(requestBody: Option[JsValue], contentType: String): Future[ApiResponse] =
    if (!contentType.equalsIgnoreCase("application/json")) {
      responseState(BadRequest("Content-Type must be application/json"))
    } else {
      requestBody match {
        case None => responseState(BadRequest("Request body is required"))

        case Some(body) =>
          body
            .validate[Notification]
            .fold(
              errors => responseState(BadRequest("Invalid JSON format")),
              notification => processNotification(notification)
            )
      }
    }

  private def processNotification(notification: Notification): Future[ApiResponse] =
    notification.seniorAccountingOfficer.fullName match {
      case _ =>
        val notificationId = s"SAONOT${TestDataFactory.randomAlphanumericId(8)}"
        responseState(NotificationComplete(notificationId))
    }

  private def validateAndCallGetRequest(id: String): Future[ApiResponse] =
    Try(UUID.fromString(id)) match {
      case Failure(_) =>
        responseState(BadRequest("Invalid UUID format"))

      case Success(_) if id == "00000000-0000-0000-0000-000000000000" =>
        responseState(NotFound("Business entity not found"))

      case Success(uuid) =>
        val stubEntity = TestDataFactory.validBusinessEntity().copy(id = uuid)
        Future.successful(ApiResponse(200, Json.toJson(stubEntity).toString()))
    }

}
