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

package uk.gov.hmrc.api.specs

import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.support.assertFieldExistsWithAValue
import uk.gov.hmrc.stubs.{ApiResponse, TestDataFactory}

import java.util.UUID

class RegisterBusinessSpec extends BaseSpec {

  "The business entity registration API" must {

    "when submitting a registration" must {
      "succeed with valid data" in {
        val businessEntity = TestDataFactory.validBusinessEntity()
        whenReady(request.put.register(businessEntity)) { response =>
          response.body must include("Business entity updated successfully")
          response.statusCode mustBe 200
        }
      }

      "reject a duplicate registration" in {
        val businessEntity = TestDataFactory.duplicateBusinessEntity()
        whenReady(request.put.register(businessEntity)) { response =>
          response.body must include("Business entity already exists")
          response.statusCode mustBe 409
        }
      }

      "reject invalid business entity data" in {
        val businessEntity = TestDataFactory.invalidBusinessEntity()
        whenReady(request.put.register(businessEntity)) { response =>
          response.body must include("Invalid business entity data")
          response.statusCode mustBe 400
        }
      }

      "require request body" in {
        whenReady(request.put.register.withNoBody()) { response =>
          response.body must include("Request body is required")
          response.statusCode mustBe 400
        }
      }

      "require valid content type header" in {
        val businessEntity = TestDataFactory.validBusinessEntity()
        whenReady(request.put.register.withInvalidContentType(businessEntity)) { response =>
          response.body must include("Content-Type must be application/json")
          response.statusCode mustBe 400
        }
      }
    }

    "when retrieving a registration" must {

      "return a business entity for a valid ID" in {
        val validUUID = UUID.randomUUID()
        whenReady(request.get(validUUID.toString)) { response =>
          assertFieldExistsWithAValue(response, "crn")
          assertFieldExistsWithAValue(response, "utr")
          assertFieldExistsWithAValue(response, "name")
          response.body must include(validUUID.toString)
          response.statusCode mustBe 200
        }
      }

      "return 'not found' for missing entity" in {
        val nonExistentId = "00000000-0000-0000-0000-000000000000"
        whenReady(request.get(nonExistentId)) { response =>
          response.body must include("Business entity not found")
          response.statusCode mustBe 404
        }
      }

      "reject invalid UUID format" in {
        whenReady(request.get("not-a-uuid")) { response =>
          response.body must include("Invalid UUID format")
          response.statusCode mustBe 400
        }
      }
    }
  }
}
