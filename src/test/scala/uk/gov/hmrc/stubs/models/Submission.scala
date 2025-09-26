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
