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

class RegistrationSpec extends BaseSpec {

  "On calling the registration API" when {

    "submitting a business entity for registration, the request" must {
      "succeed when valid data is provided" in {
        val businessEntity = TestDataFactory.validBusinessEntity()
        whenReady(request.put.registrationApi(businessEntity)) { response =>
          response.body must include("Business entity updated successfully")
          response.statusCode mustBe 200
        }
      }

      "fail when the business entity is already registered" in {
        val businessEntity = TestDataFactory.duplicateBusinessEntity()
        whenReady(request.put.registrationApi(businessEntity)) { response =>
          response.body must include("Business entity already exists")
          response.statusCode mustBe 409
        }
      }

      "fail when invalid business entity data is provided" in {
        val businessEntity = TestDataFactory.invalidBusinessEntity()
        whenReady(request.put.registrationApi(businessEntity)) { response =>
          response.body must include("Invalid business entity data")
          response.statusCode mustBe 400
        }
      }

      "fail if attempted without providing a request body" in {
        whenReady(request.put.registrationApi.withNoBody()) { response =>
          response.body must include("Request body is required")
          response.statusCode mustBe 400
        }
      }

      "fail if attempted with an invalid content-type header" in {
        val businessEntity = TestDataFactory.validBusinessEntity()
        whenReady(request.put.registrationApi.withInvalidContentType(businessEntity)) { response =>
          response.body must include("Content-Type must be application/json")
          response.statusCode mustBe 400
        }
      }
    }

    "retrieving a registration, the request" must {

      "successfully return a business entity when a valid Id is provided" in {
        val validUUID = UUID.randomUUID()
        whenReady(request.get(validUUID.toString)) { response =>
          assertFieldExistsWithAValue(response, "crn")
          assertFieldExistsWithAValue(response, "utr")
          assertFieldExistsWithAValue(response, "name")
          response.body must include(validUUID.toString)
          response.statusCode mustBe 200
        }
      }

      "fail to return a business entity if the Id is valid but not present in the database" in {
        val nonExistentId = "00000000-0000-0000-0000-000000000000"
        whenReady(request.get(nonExistentId)) { response =>
          response.body must include("Business entity not found")
          response.statusCode mustBe 404
        }
      }

      "fail if attempted with an invalid UUID" in {
        whenReady(request.get("not-a-uuid")) { response =>
          response.body must include("Invalid UUID format")
          response.statusCode mustBe 400
        }
      }
    }
  }
}
