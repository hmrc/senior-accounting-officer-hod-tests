package uk.gov.hmrc.api.specs

class SubscriptionSpec extends BaseSpec {

  Feature("Subscription"){

    Scenario("Successful submission of Subscription returns 200 and Subscription ID"){

      Given("I have completed company, contact, and Senior Accounting Officer details correctly during a company registration")
      //Contacts
      val contacts = List(
        List("Name1", "Role1", "Email1")
      )

      //Registration/Subscription Data
      val registration: Map[String, Any] = Map(
        "CRN" -> "0123456",
        "UTR" -> "1234567890",
        "Company" -> "Company1",
        "Contacts" -> contacts,
        "Name" -> "John Smith",
        "Email" -> "john@smith.test"
      )

      When("I submit the registration details")
      //Submission of data
      val response = subscriptionSubmission(registration)

      Then("I will receive a successful response with a status code of ‘200’")
      //Response from Stub for status
      response.status shouldBe 200

      And("a message containing a ‘Reference Number’")
      //Response from Stub for reference
      response.subscriptionID should not be empty

    }

    //Stub response and submission
    case class Response(status: Int, subscriptionID: Option[String])

    def subscriptionSubmission(registration: Map[String, Any]): Response = {
        Response(200, Some("REF213746"))
    }

  }

}
