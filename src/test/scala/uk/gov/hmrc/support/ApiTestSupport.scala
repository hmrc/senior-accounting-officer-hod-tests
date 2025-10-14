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
import uk.gov.hmrc.stubs.{ApiResponse, ApiStubs}
import uk.gov.hmrc.stubs.models.{BusinessEntity, Certificate, Notification}
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
  def registrationApi: Register = Register(baseUrl)
  def notifyApi: Notify         = Notify(baseUrl)
  def certifyApi: Certify       = Certify(baseUrl)
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

  def apply(certificate: Certificate, role: String): Future[ApiResponse] =
    ApiStubs.call(
      method = "PUT",
      url = path,
      body = Some(Json.toJson(certificate)),
      basicAuth = role
    )
}

final case class Notify(baseUrl: String) {
  val path = s"$baseUrl/notification"

  def apply(notification: Notification): Future[ApiResponse] =
    ApiStubs.call(
      method = "PUT",
      url = path,
      body = Some(Json.toJson(notification))
    )

  def putWithNoBody(): Future[ApiResponse] =
    ApiStubs.call(
      method = "PUT",
      url = path,
      body = None
    )

  def putWithInvalidContentType(notification: Notification): Future[ApiResponse] =
    ApiStubs.call(
      method = "PUT",
      url = path,
      body = Some(Json.toJson(notification)),
      contentType = "text/plain"
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
}
