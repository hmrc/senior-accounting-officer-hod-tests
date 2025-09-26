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

package uk.gov.hmrc.stubs.models

import java.time.Instant
import play.api.libs.json._
import java.time.temporal.ChronoUnit

final case class Submission(
  accountingPeriod: Option[AccountingPeriod],
  receivedDate: Instant = Instant.now().truncatedTo(ChronoUnit.MINUTES)
//                             progress: Progress = Progress.Subscribed,
)

case class AccountingPeriod(
  startDate: Instant,
  endDate: Instant,
  dueDate: Instant
)

object Submission {
  implicit val reads: Submission = new Submission(
    Some(
      AccountingPeriod(Instant.now(), Instant.now.plus(2, ChronoUnit.MINUTES), Instant.now.plus(1, ChronoUnit.MINUTES))
    )
  )

  implicit val format: OFormat[Submission] = Json.format[Submission]
}

object AccountingPeriod {
  implicit val format: OFormat[AccountingPeriod] = Json.format[AccountingPeriod]
}
