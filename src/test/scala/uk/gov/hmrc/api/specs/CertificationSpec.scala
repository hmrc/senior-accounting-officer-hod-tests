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

import uk.gov.hmrc.stubs.TestDataFactory
import uk.gov.hmrc.support.assertFieldExistsWithAValue

class CertificationSpec extends BaseSpec {

  "On calling the certification API" when {

    "submitting a new certificate, the request" must {
      "succeed when the user is an administrator and provides valid data" in {
        val certificate = TestDataFactory.validCertificate()
        whenReady(request.put.certifyApi(certificate, "Administrator")) { response =>
          assertFieldExistsWithAValue(response, "certificateSafeId")
          response.body must include("Certification complete")
          response.statusCode mustBe 201
        }
      }
    }

    "amending a certificate, the request" must {
      "succeed when the user is an administrator and provides valid data" in {
        val certificate = TestDataFactory.validCertificate(id = Some("EXISTING_CERT_ID"))
        whenReady(request.put.certifyApi(certificate, "Administrator")) { response =>
          assertFieldExistsWithAValue(response, "certificateSafeId")
          response.body must include("Certificate updated")
          response.statusCode mustBe 200
        }
      }

      "fail if attempted with valid data but an invalid role" in {
        val certificate = TestDataFactory.validCertificate(id = Some("EXISTING_CERT_ID"))
        whenReady(request.put.certifyApi(certificate, "Non-Administrator")) { response =>
          response.body must include("Unauthorized")
          response.statusCode mustBe 401
        }
      }
    }
  }
}
