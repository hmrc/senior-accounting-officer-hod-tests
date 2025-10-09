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
import uk.gov.hmrc.stubs.TestDataFactory

class SubmitNotificationSpec extends BaseSpec {

  "The notification API" must {

    "when submitting a notification" must {
      "succeed with valid data" in {
        val notification = TestDataFactory.validNotification()
        whenReady(request.put.notifyApi(notification)) { response =>
          response.body must include("Notification complete")
          response.statusCode mustBe 200
        }
      }

      "require request body" in {
        whenReady(request.put.notifyApi.putWithNoBody()) { response =>
          response.body must include("Request body is required")
          response.statusCode mustBe 400
        }
      }

      "require valid content type header" in {
        val notification = TestDataFactory.validNotification()
        whenReady(request.put.notifyApi.putWithInvalidContentType(notification)) { response =>
          response.body must include("Content-Type must be application/json")
          response.statusCode mustBe 400
        }
      }
    }

  }
}
