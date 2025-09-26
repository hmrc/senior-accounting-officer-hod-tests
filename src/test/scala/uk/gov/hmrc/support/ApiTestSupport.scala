package uk.gov.hmrc.support

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import uk.gov.hmrc.stubs.{BusinessEntityApiStub, ApiResponse}
import uk.gov.hmrc.stubs.models.BusinessEntity
import play.api.libs.json.Json
import scala.concurrent.Future

trait ApiTestSupport extends ScalaFutures {
  
    // Clean request DSL
    object request {
      def put(entity: BusinessEntity): Future[ApiResponse] = {
        BusinessEntityApiStub.call(
          method = "PUT",
          url = "/senior-accounting-officer-hod/business-entity",
          body = Some(Json.toJson(entity))
        )
      }

      def putWithNoBody(): Future[ApiResponse] = {
        BusinessEntityApiStub.call(
          method = "PUT",
          url = "/senior-accounting-officer-hod/business-entity",
          body = None
        )
      }

      def putWithInvalidContentType(entity: BusinessEntity): Future[ApiResponse] = {
        BusinessEntityApiStub.call(
          method = "PUT",
          url = "/senior-accounting-officer-hod/business-entity",
          body = Some(Json.toJson(entity)),
          contentType = "text/plain"
        )
      }
      
      def get(entityId: String): Future[ApiResponse] = {
        BusinessEntityApiStub.call(
          method = "GET",
          url = s"/senior-accounting-officer-hod/business-entity/$entityId"
        )
      }
      
      def get(entityId: java.util.UUID): Future[ApiResponse] = get(entityId.toString)
    }
  }