import javax.inject.Inject

import play.api.i18n.Messages
import play.api.mvc._
import play.api.test._

import scala.concurrent.Future
import com.dsleng.controllers.ApplicationController
import com.dsleng.utils.auth.DefaultEnv
import com.dsleng.models.security.User
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.test._
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}

// import utils.silhouette._
// import com.google.inject.AbstractModule
// import net.codingwell.scalaguice.ScalaModule
// import com.mohiva.play.silhouette.api.{ Environment, LoginInfo }
// import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
// import com.mohiva.play.silhouette.test._
// import org.specs2.mock.Mockito
// import org.specs2.specification.Scope
// import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits._
// import play.api.test.{ FakeRequest, PlaySpecification, WithApplication }


class AuthSpec extends PlaySpecification {
    "The `isAuthenticated` method" should {
        "return status 200 if authenticator and identity was found" in new WithApplication {
            val identity = User(None, LoginInfo("facebook", "apollonia.vanova@watchmen.com"), "user1", "user1@test.com", "user1", "user1last", None, true)
            implicit val env = FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))
            
            val request = FakeRequest().withAuthenticator(identity.loginInfo)
            println(request);
            println(s"    content-type: ${request.contentType}")
            println(s"    headers: ${request.headers}")
            println(s"    body: ${request.body}")
            println(s"    query string: ${request.rawQueryString}")
            val controller = app.injector.instanceOf[ApplicationController]
            val result = controller.isAuthenticated(request)

            status(result) must equalTo(OK)
        }
  }
    /*
sequential
  "The `user` method" should {
    "return status 401 if no authenticator was found" in new WithApplication {
      //val identity = User(LoginInfo("facebook", "apollonia.vanova@watchmen.com"))
      val identity = User(None, LoginInfo("facebook", "apollonia.vanova@watchmen.com"), "user1", "user1@test.com", "user1", "user1last", None, true)
      implicit val env = FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))
      println("logging info",identity.loginInfo)
      //val request = FakeRequest()
      // Providing a fake Identity
      val request = FakeRequest()
        .withAuthenticator(LoginInfo("xing", "comedian@watchmen.com"))


      val controller = app.injector.instanceOf[ApplicationController]
      val result = controller.user(request)
       println(contentAsString(result))
      status(result) must equalTo(UNAUTHORIZED)
    }
  }
  "The `user` method" should {
    "return status 200 if authenticator and identity was found" in new WithApplication {
      //val identity = User(LoginInfo("facebook", "apollonia.vanova@watchmen.com"))
      val identity = User(None, LoginInfo("facebook", "apollonia.vanova@watchmen.com"), "user1", "user1@test.com", "user1", "user1last", None, true)
    
      implicit val env = FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))
      val request = FakeRequest(GET,"/api/auth/user").withAuthenticator(identity.loginInfo)

      val controller = app.injector.instanceOf[ApplicationController]
      val result = controller.user(request)
      println(contentAsString(result))
      status(result) must equalTo(OK)
    }
  }

  "The `isAuthenticated` method" should {
    "return status 401 if no authenticator was found" in new WithApplication {
      //val identity = User(LoginInfo("facebook", "apollonia.vanova@watchmen.com"))
      val identity = User(None, LoginInfo("facebook", "apollonia.vanova@watchmen.com"), "user1", "user1@test.com", "user1", "user1last", None, true)
      implicit val env = FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))
      // Providing a fake Identity
      val request = FakeRequest()
        .withAuthenticator(LoginInfo("xing", "comedian@watchmen.com"))


      val controller = app.injector.instanceOf[ApplicationController]
      val result = controller.isAuthenticated(request)

      status(result) must equalTo(UNAUTHORIZED)
    }
  }
   "The `isAuthenticated` method" should {
    "return status 200 if authenticator and identity was found" in new WithApplication {
      val identity = User(None, LoginInfo("facebook", "apollonia.vanova@watchmen.com"), "user1", "user1@test.com", "user1", "user1last", None, true)
      implicit val env = FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo)

      val controller = app.injector.instanceOf[ApplicationController]
      val result = controller.isAuthenticated(request)

      status(result) must equalTo(OK)
    }
  }
  "The `retrieve` method of the `FakeIdentityService`" should {
    "return the identity for the given login info" in {
      val loginInfo = LoginInfo("test", "test")
      val identity = FakeIdentity(loginInfo)
      val service = new FakeIdentityService[FakeIdentity](loginInfo -> identity)

      await(service.retrieve(loginInfo)) must beSome(identity)
    }

    "return None if no identity could be found for the given login info" in {
      val loginInfo = LoginInfo("test", "test")
      val service = new FakeIdentityService[FakeIdentity]()

      await(service.retrieve(loginInfo)) must beNone
    }
  }
  */
}
