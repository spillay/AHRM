package mongo

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.dsleng.models.security.User
import org.scalatest.BeforeAndAfter
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import reactivemongo.bson.BSONDocument
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class DBIntegrationSpec extends PlayWithMongoSpec with BeforeAndAfter {
  var users: Future[JSONCollection] = _

  before {
    //Init DB
    await {
      //users = reactiveMongoApi.database.map(_.collection("user"))
      users = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

      val avatarURL = None
      val u1 = User(id = None,new LoginInfo("1","empty"),
              "username",
              "email",
              "firstName" ,
              "lastName",
              avatarURL,
              false)
      users.flatMap(_.insert(ordered=false).one(u1))
//      users.flatMap(_.insert[User](ordered = false).many(List( 
//          User(id = None,new LoginInfo("1","empty"),
//              "username",
//              "email",
//              "firstName" ,
//              "lastName",
//              avatarURL,
//              false),
//           User(id = None,new LoginInfo("12","empty2"),
//              "username2",
//              "email2",
//              "firstName2" ,
//              "lastName2",
//              avatarURL,
//              false)
//      )))
    }
  }

  after {
    //clean DB
    users.flatMap(_.drop(failIfNotFound = false))
  }
  
//  "Authenticate User" in {
//      val Some(result) = route(app, FakeRequest(POST, "//api/auth/signup").withJsonBody(Json.obj(
//          "identifier" -> "james.bond",
//          "password" -> "this!Password!Is!Very!Very!Strong!",
//          "email" -> "james.bond@test.com",
//          "firstName" -> "James",
//          "lastName" -> "Bond"
//      )))
//      println("result" + result);
//      //val resultList = contentAsJson(result).as[List[User]]
//      //resultList.length mustEqual 4
//      //status(result) mustBe OK
//  }
  "check" in {
      val query = BSONDocument()
      //val Some(result) = await(users.flatMap(_.find(query).one[User]))
      val result = await(users.flatMap(
          _.find(query,projection = Option.empty[BSONDocument]).cursor[User]().collect[List](100, Cursor.FailOnError[List[User]]())
          )
      );
      result.length mustEqual 1
      println("result " + result);
  }
}