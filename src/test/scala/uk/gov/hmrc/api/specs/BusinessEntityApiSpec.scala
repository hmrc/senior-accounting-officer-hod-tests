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
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.stubs.TestDataFactory
import uk.gov.hmrc.support.ApiTestSupport
import java.util.UUID

class BusinessEntityApiSpec extends AnyWordSpec with Matchers with ApiTestSupport {

  "The business entity registration API" must {

    "when submitting a registration" must {
      "succeed with valid data" in {
        val businessEntity = TestDataFactory.validBusinessEntity()
        whenReady(request.put(businessEntity)) { response =>
          response.body should include("Business entity updated successfully")
          response.statusCode shouldBe 200
        }
      }

      "reject a duplicate registration" in {
        val businessEntity = TestDataFactory.duplicateBusinessEntity()
        whenReady(request.put(businessEntity)) { response =>
          response.body should include("Business entity already exists")
          response.statusCode shouldBe 409
        }
      }

      "reject invalid business entity data" in {
        val businessEntity = TestDataFactory.invalidBusinessEntity()
        whenReady(request.put(businessEntity)) { response =>
          response.body should include("Invalid business entity data")
          response.statusCode should equal(400)
        }
      }

      "require request body" in {
        whenReady(request.putWithNoBody()) { response =>
          response.body should include("Request body is required")
          response.statusCode shouldBe 400
        }
      }

      "require valid content type header" in {
        val businessEntity = TestDataFactory.validBusinessEntity()
        whenReady(request.putWithInvalidContentType(businessEntity)) { response =>
          response.body should include("Content-Type must be application/json")
          response.statusCode shouldBe 400
        }
      }
    }

    "when retrieving a registration" must {

      "return a business entity for a valid ID" in {
        val validUUID = UUID.randomUUID() // Once we are able to add entities, update this code with a valid ID
        whenReady(request.get(validUUID)) { response =>
          response.body should include(validUUID.toString)
          response.body should include("Test Company Ltd")
          response.statusCode shouldBe 200
        }
      }

      "return 'not found' for missing entity" in {
        val nonExistentId = "00000000-0000-0000-0000-000000000000"
        whenReady(request.get(nonExistentId)) { response =>
          response.body should include("Business entity not found")
          response.statusCode shouldBe 404
        }
      }

      "reject invalid UUID format" in {
        whenReady(request.get("not-a-uuid")) { response =>
          response.body should include("Invalid UUID format")
          response.statusCode shouldBe 400
        }
      }
    }
  }
}
