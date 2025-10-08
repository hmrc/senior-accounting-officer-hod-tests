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

import org.scalatest.Assertions.{fail, succeed}
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.stubs.{ApiResponse, ApiStubs, NotificationApiStub}
import uk.gov.hmrc.stubs.models.{BusinessEntity, Notification, Certificate}
import play.api.libs.json.{JsArray, JsBoolean, JsDefined, JsNumber, JsObject, JsString, JsUndefined, Json}
import org.scalatest.matchers.must.Matchers.mustBe

import scala.concurrent.Future

trait ApiTestSupport extends ScalaFutures {
  val request: RequestApi = RequestApi()
}

final case class RequestApi() {
  private val baseUrl = "/senior-accounting-officer-hod"

  def put: PutRequest = PutRequest(baseUrl)

  def get(entityId: String): Future[ApiResponse] =
    ApiStubs.call(
      method = "GET",
      url = s"$baseUrl/business-entity/$entityId"
    )
}

final case class PutRequest(baseUrl: String) {
  def register: Register = Register(baseUrl)
  def certify: Certify   = Certify(baseUrl)
}

final case class Register(baseUrl: String) {
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

final case class Certify(baseUrl: String) {
  val path: String = s"$baseUrl/certification"

  def apply(certificate: Certificate): Future[ApiResponse] =
    ApiStubs.call(
      method = "PUT",
      url = path,
      body = Some(Json.toJson(certificate))
    )
}

def assertFieldExistsWithAValue(response: ApiResponse, fieldName: String) = {
  val jsLookup = Json.parse(response.body) \ fieldName
  jsLookup match {
    case JsDefined(jsValue) =>
      jsValue match {
        case JsString(s)   => s.trim.nonEmpty mustBe true
        case JsNumber(n)   => n != null mustBe true
        case JsBoolean(_)  => succeed
        case JsArray(arr)  => arr.nonEmpty mustBe true
        case JsObject(obj) => obj.nonEmpty mustBe true
        case _             => fail(s"$fieldName has unsupported type or is empty!")
      }
    case JsUndefined()      => fail(s"$fieldName is missing in the response!")
    case _                  => fail(s"Unexpected JsLookupResult for $fieldName")
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
