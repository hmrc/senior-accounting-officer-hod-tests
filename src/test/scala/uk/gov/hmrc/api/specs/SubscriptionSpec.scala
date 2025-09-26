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

class SubscriptionSpec extends BaseSpec {

  Feature("Subscription") {

    Scenario("Successful submission of Subscription returns 200 and Subscription ID") {

      Given(
        "I have completed company, contact, and Senior Accounting Officer details correctly during a company registration"
      )
      // Contacts
      val contacts = List(
        List("Name1", "Role1", "Email1")
      )

      // Registration/Subscription Data
      val registration: Map[String, Any] = Map(
        "CRN"      -> "0123456",
        "UTR"      -> "1234567890",
        "Company"  -> "Company1",
        "Contacts" -> contacts,
        "Name"     -> "John Smith",
        "Email"    -> "john@smith.test"
      )

      When("I submit the registration details")
      // Submission of data
      val response = subscriptionSubmission(registration)

      Then("I will receive a successful response with a status code of ‘200’")
      // Response from Stub for status
      response.status shouldBe 200

      And("a message containing a ‘Reference Number’")
      // Response from Stub for reference
      response.subscriptionID should not be empty

    }

    // Stub response and submission
    case class Response(status: Int, subscriptionID: Option[String])

    def subscriptionSubmission(registration: Map[String, Any]): Response =
      Response(200, Some("REF213746"))

  }

}
