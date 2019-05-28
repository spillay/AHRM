package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api._
//import play.api.cache.CacheApi
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
//import util.FakeCache
import com.dsleng.emo.{ EmoComponent, EmoComponentImpl }
import org.specs2.mutable._
import org.scalatest._
import Matchers._


class WebSpec extends PlaySpec with GuiceOneAppPerSuite {
  implicit override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "play.modules.enabled" -> List("com.dsleng.emo.EmoModule"),
      "mongodb.uri" -> "mongodb://localhost:27017/test")
    .bindings(bind[ApplicationLifecycle].to[DefaultApplicationLifecycle])
    .build

  "respond to the index Action" in new WithApplication {
    val controller = app.injector.instanceOf[HomeController]
    val result = controller.index()(FakeRequest())

    //status(result) must equalTo(OK)
    //contentType(result) must beSome("text/plain")
    contentAsString(result) must contain("Hello Bob")
  }
}