package uk.gov.hmrc.stubs.models

import play.api.libs.json.*

import scala.util.Try

enum Progress {
  case Subscribed, Assigned, Notified, Certified
}

given Reads[Progress] = Reads {
  case JsString(value) =>
    Try(Progress.valueOf(value))
      .map(JsSuccess(_))
      .getOrElse(JsError(s"Unknown progress value: $value"))

  case _ => JsError("String value expected for progress")
}

given Writes[Progress] = Writes { progress =>
  JsString(progress.toString)
}
