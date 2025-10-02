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

package uk.gov.hmrc.stubs.enums

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*

class ContactTypeOrderSpec extends AnyWordSpec with Matchers {

  "The ContactTypeOrder enum" must {

    "serialize and deserialize all valid contact type order values" in {
      val validContactTypeOrderValues = List(ContactTypeOrder.First, ContactTypeOrder.Second, ContactTypeOrder.Third)

      validContactTypeOrderValues.foreach { value =>
        val json   = Json.toJson(value)
        val result = json.validate[ContactTypeOrder]
        result mustBe JsSuccess(value)
      }
    }

    "reject any invalid string values" in {
      val invalidStringValues = List("Zero", "Fourth", "Fifth", "", "FIRST", "2nd")

      invalidStringValues.foreach { value =>
        val result = JsString(value).validate[ContactTypeOrder]
        result mustBe a[JsError]
        result.asInstanceOf[JsError].errors.head._2.head.message must equal(s"Unknown ContactTypeOrder value: $value")
      }
    }

    "reject non-string JSON values" in {
      val invalidNonStringValues = List(JsNumber(1), JsBoolean(true), JsNull, Json.obj("key" -> "value"))

      invalidNonStringValues.foreach { value =>
        val result = value.validate[ContactTypeOrder]
        result mustBe a[JsError]
        result.asInstanceOf[JsError].errors.head._2.head.message must include("ContactTypeOrder must be a string")
      }
    }
  }
}
