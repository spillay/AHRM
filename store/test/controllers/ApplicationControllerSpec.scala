import org.specs2.mutable.Specification
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import play.api.test.{ FakeRequest, WithApplication }
import com.dsleng.controllers.ApplicationController
import play.mvc.Http.HeaderNames
import play.api.libs.json.Json

class ApplicationControllerSpec extends Specification {
  "ApplicationController POST" should {

    "render the index page from the application" in new WithApplication() {

      val controller = app.injector.instanceOf[ApplicationController]

      val requestNode = Json.toJson(Map("name" -> "Testname"))
    //   val request = FakeRequest().copy(body = requestNode)
    //     .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json");

      //val request = FakeRequest().withCSRFToken
      val request = FakeRequest(POST, "/api/query")
        .withJsonBody(Json.parse("""{ "field": "value" }"""))
        .withHeaders("Authorization" -> "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxLWF4TWRkVWt1UjFxZkZLakF2QmNqd2pJRXlFZGV3ZjZCTVNWS2h1WVdiUWxnTG5FNHhuMDVmZGNRWmdxY0FhM0h6dHJoczlWeEczc3VJNU1UNWVaOHk3NGVPUT09IiwiaXNzIjoicGxheS1zaWxob3VldHRlIiwiZXhwIjoxNTI3OTIwMjI0LCJpYXQiOjE1Mjc4NzcwMjQsImp0aSI6IjQ1ZDg0OGQwMmRjOWZjZWY0NWNiNWYzMGEzNjEyZGFhZWY5ZWQ4YTBkNDQxZWY0ZGZmMWYyY2M2Y2MyM2E1MjM0YTNhMWM0YWQxNzI2NGU0YzcyNGUyNDM0OTE4MTI4YWNiYjk3OWNhZjc5ZWRmNzU5NTgxMjFlYjI1Nzc1NDMwMDMxMzIwYmI0ZWI3ZmNmYThiODkxZTM4ZWNiY2VlOWRmZDViNmMxMWIyODA2ZDVkZWI0MTI5ZjBlZDE5MGFhYjdiNjc3MmJmOTY2YjE0NzYwZDE3YjVkOTZkNTdlNWFlZTNkNWM5Mzg1NGNhOGNhYWExZWMyMWY4NGRiZmI1MWQifQ.O0X4-6eFlYnnZTyX2VL6VZ741XtvbvY9xTMFsLEfATw")
        .withCSRFToken
      val result = controller.query().apply(request)
      println("results")
      println(result)
      status(result) must beEqualTo(OK)
      contentType(result) must beSome("application/json")
      println(contentAsString(result))
    }
  }
}