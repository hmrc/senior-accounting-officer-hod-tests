package uk.gov.hmrc.stubs.models

package stubs.models

import play.api.libs.json.{Json, OFormat}
import java.time.Instant

final case class AccountingPeriod(startDate: Instant, endDate: Instant, dueDate: Instant)

object AccountingPeriod {
  implicit val format: OFormat[AccountingPeriod] = Json.format[AccountingPeriod]
}
