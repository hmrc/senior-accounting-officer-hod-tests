import sbt.*

object Dependencies {

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "api-test-runner" % "0.9.0" % Test,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0" % Test,
    "org.mockito" % "mockito-core" % "5.19.0" % Test,
    "io.spray" %% "spray-json" % "1.3.6"
  )

}
