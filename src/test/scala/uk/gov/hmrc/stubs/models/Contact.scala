package uk.gov.hmrc.stubs.models

import play.api.libs.json.{Json, OFormat}

final case class Contact (
                           name: String, 
                           role: String, 
                           email: String, 
                           phone: String, 
                           order: String
                         )

object Contact {
  implicit val format: OFormat[Contact] = Json.format[Contact]
}
