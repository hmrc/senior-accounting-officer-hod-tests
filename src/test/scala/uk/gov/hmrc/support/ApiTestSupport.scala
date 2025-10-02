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
import uk.gov.hmrc.stubs.{ApiResponse, BusinessEntityApiStub}
import uk.gov.hmrc.stubs.models.BusinessEntity
import play.api.libs.json.Json
import scala.concurrent.Future

trait ApiTestSupport extends ScalaFutures {

  object request {
    private val baseUrl = "/senior-accounting-officer-hod/business-entity"

    def put(entity: BusinessEntity): Future[ApiResponse] =
      BusinessEntityApiStub.call(
        method = "PUT",
        url = baseUrl,
        body = Some(Json.toJson(entity))
      )

    def putWithNoBody(): Future[ApiResponse] =
      BusinessEntityApiStub.call(
        method = "PUT",
        url = baseUrl,
        body = None
      )

    def putWithInvalidContentType(entity: BusinessEntity): Future[ApiResponse] =
      BusinessEntityApiStub.call(
        method = "PUT",
        url = baseUrl,
        body = Some(Json.toJson(entity)),
        contentType = "text/plain"
      )

    def get(entityId: String): Future[ApiResponse] =
      BusinessEntityApiStub.call(
        method = "GET",
        url = s"$baseUrl/$entityId"
      )

    def get(entityId: java.util.UUID): Future[ApiResponse] = get(entityId.toString)
  }
}
