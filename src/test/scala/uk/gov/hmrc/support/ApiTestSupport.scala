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

package uk.gov.hmrc.support

import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.stubs.{ApiResponse, ApiStubs, NotificationApiStub}
import uk.gov.hmrc.stubs.models.{BusinessEntity, Notification, Certificate}
import play.api.libs.json.Json

import scala.concurrent.Future

trait ApiTestSupport extends ScalaFutures {

  private val baseUrl = "/senior-accounting-officer-hod"

  object request extends RequestApi

  case class RequestApi() {
    def put: PutRequest = PutRequest()

    def get(entityId: String): Future[ApiResponse] =
      ApiStubs.call(
        method = "GET",
        url = s"$baseUrl/business-entity/$entityId"
      )
  }

  case class PutRequest() {
    def register: Register = Register()
    def certify: Certify   = Certify()
  }

  case class Register() {
    val path: String = s"$baseUrl/business-entity"

    def apply(entity: BusinessEntity): Future[ApiResponse] =
      ApiStubs.call(
        method = "PUT",
        url = path,
        body = Some(Json.toJson(entity))
      )

    def withNoBody(): Future[ApiResponse] =
      ApiStubs.call(
        method = "PUT",
        url = path,
        body = None
      )

    def withInvalidContentType(entity: BusinessEntity): Future[ApiResponse] =
      ApiStubs.call(
        method = "PUT",
        url = path,
        body = Some(Json.toJson(entity)),
        contentType = "text/plain"
      )
  }

  case class Certify() {
    val path: String = s"$baseUrl/certification"

    def apply(certificate: Certificate): Future[ApiResponse] =
      ApiStubs.call(
        method = "PUT",
        url = path,
        body = Some(Json.toJson(certificate))
      )
  }

  object requestNotification {
    private val baseUrl = "/senior-accounting-officer-hod/notification"

    def put(notify: Notification): Future[ApiResponse] =
      NotificationApiStub.call(
        method = "PUT",
        url = baseUrl,
        body = Some(Json.toJson(notify))
      )

    def putWithNoBody(): Future[ApiResponse] =
      NotificationApiStub.call(
        method = "PUT",
        url = baseUrl,
        body = None
      )

    def putWithInvalidContentType(notify: Notification): Future[ApiResponse] =
      NotificationApiStub.call(
        method = "PUT",
        url = baseUrl,
        body = Some(Json.toJson(notify)),
        contentType = "text/plain"
      )
  }
}
