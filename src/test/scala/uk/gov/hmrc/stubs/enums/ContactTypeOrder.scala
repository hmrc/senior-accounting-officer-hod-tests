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

import play.api.libs.json.*

import scala.util.Try

enum ContactTypeOrder {
  case First, Second, Third
}

object ContactTypeOrder {

  given Format[ContactTypeOrder] = new Format[ContactTypeOrder] {
    def writes(contactTypeOrder: ContactTypeOrder): JsValue =
      JsString(contactTypeOrder.toString)

    def reads(json: JsValue): JsResult[ContactTypeOrder] = json match {
      case JsString(value) =>
        Try(ContactTypeOrder.valueOf(value))
          .map(JsSuccess(_))
          .getOrElse(JsError(s"Unknown ContactTypeOrder value: $value"))

      case _ => JsError("ContactTypeOrder must be a string (e.g. \"Second\")")
    }
  }
}
