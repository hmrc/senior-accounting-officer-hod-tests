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

package uk.gov.hmrc.adt

import uk.gov.hmrc.stubs.ApiResponse

import scala.concurrent.Future

sealed trait ResponseType

// Success responses
case class CertificateUpdated(id: String) extends ResponseType
case class CertificationComplete(id: String) extends ResponseType
case class BusinessEntityUpdated(id: String) extends ResponseType
case object NotificationComplete extends ResponseType

// Failure responses
case class BadRequest(message: String) extends ResponseType
case object Unauthorized extends ResponseType
case class NotFound(message: String) extends ResponseType
case class Conflict(message: String) extends ResponseType

object ResponseType {
  def responseState(responseType: ResponseType): Future[ApiResponse] =
    responseType match {
      case CertificateUpdated(id) =>
        Future.successful(ApiResponse(200, s"""{"message":"Certificate updated","certificateSafeId":"$id"}"""))

      case CertificationComplete(id) =>
        Future.successful(ApiResponse(201, s"""{"message":"Certification complete","certificateSafeId":"$id"}"""))

      case BusinessEntityUpdated(id) =>
        Future.successful(ApiResponse(200, s"""{"message":"Business entity updated successfully","id":"$id"}"""))

      case NotificationComplete =>
        Future.successful(ApiResponse(200, s"""{"message":"Notification complete"}"""))

      case BadRequest(message) =>
        Future.successful(ApiResponse(400, s"""{"error":"$message"}"""))

      case Unauthorized =>
        Future.successful(ApiResponse(401, s"""{"error":"Unauthorized"}"""))

      case NotFound(message) =>
        Future.successful(ApiResponse(404, s"""{"error":"$message"}"""))

      case Conflict(message) =>
        Future.successful(ApiResponse(409, s"""{"error":"$message"}"""))
    }
}
