package uk.gov.hmrc.stubs.models

import play.api.libs.json.{Json, OFormat}
import java.util.UUID
import java.time.Instant

final case class BusinessEntity(
  id: UUID,
  crn: String,
  utr: String,
  name: String,
  contacts: List[Contact] = List.empty,
  submissions: Option[List[Submission]] = None,
  createdAt: Instant
)

object BusinessEntity {
  implicit val format: OFormat[BusinessEntity] = Json.format[BusinessEntity]
}
