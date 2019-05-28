package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneServerPerSuiteWithComponents
import play.api._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

import scala.concurrent.Future

import com.dsleng.emo.EmoComponentImpl

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec  {

/*
  override def components: BuiltInComponents = new BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

    import play.api.mvc.Results
    import play.api.routing.Router
    import play.api.routing.sird._

    lazy val router: Router = Router.from({
      case GET(p"/") => defaultActionBuilder {
        Results.Ok("success!")
      }
    })

    override lazy val configuration: Configuration = context.initialConfiguration ++ Configuration("foo" -> "bar", "ehcacheplugin" -> "disabled")
  }
  "The OneServerPerSuiteWithComponents trait" must {
    "provide an Application" in {
      import play.api.test.Helpers.{GET, route}
      val Some(result): Option[Future[Result]] = route(app, FakeRequest(GET, "/"))
      Helpers.contentAsString(result) must be("success!")
    }
    "override the configuration" in {
      app.configuration.getOptional[String]("foo") mustBe Some("bar")
    }
  }
  "HomeController GET" should {

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/").withJsonBody(Json.parse("""{"bad": "field"}"""))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }
    "process entropy" in {
      val controller = inject[HomeController]
      val home = controller.getEntropy().apply(FakeRequest(GET, "/entropy"))
      status(home) mustBe OK
    }
  }
  * */
}
