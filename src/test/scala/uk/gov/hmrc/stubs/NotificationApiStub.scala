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

import uk.gov.hmrc.stubs.models.Notification
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

//class ApiResponse(statusCode: Int, body: String)

object NotificationApiStub {

//  private val basePath = "/senior-accounting-officer-hod/notification"

  def call(
    method: String,
    url: String,
    body: Option[JsValue] = None,
    contentType: String = "application/json"
  ): Future[ApiResponse] =
    (method.toUpperCase, url) match {
      case ("PUT", basePath) =>
        validateAndCallPutRequest(body, contentType)

    }

  private def validateAndCallPutRequest(requestBody: Option[JsValue], contentType: String): Future[ApiResponse] = {
    if (contentType != "application/json") return badRequest("Content-Type must be application/json")

    requestBody match {
      case None => badRequest("Request body is required")

      case Some(body) =>
        body
          .validate[Notification]
          .fold(
            errors => badRequest("Invalid JSON format"),
            notification => processNotification(notification)
          )
    }
  }

  private def processNotification(notification: Notification): Future[ApiResponse] =
    notification.nominatedSAO.fullName match {
      case "" => badRequest("Invalid notification data")

      case _ =>
        Future.successful(
          ApiResponse(200, s"""{"message":"Notification complete"}""")
        )
    }

  private def badRequest(message: String): Future[ApiResponse] =
    Future.successful(ApiResponse(400, errorMessage(message)))

  private def errorMessage(message: String): String = s"""{"error":"$message"}"""

}
