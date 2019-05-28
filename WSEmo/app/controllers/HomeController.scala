package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._
import com.dsleng.emo.EmoComponent
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import play.api.libs.json._
import akka.actor.ActorSystem
import scala.concurrent._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

  
@Singleton
class HomeController @Inject()(cc: ControllerComponents,ec: EmoComponent,ws: WSClient) extends AbstractController(cc)   {
   import ExecutionContext.Implicits.global
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    val data = "{\"text\":\"i sure hope we make good use of the bad news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor weird excellencies pleasant\"}"
    val js = Json.parse(data)
    // TODO: Fix if 5001 is down throw an error
    ws.url("http://localhost:5001/emo/tokens").post(js).map{response =>
      val rs = Json.parse(response.body)
      val tokens = (rs \ "tokens" ).get
      val stoks = Json.parse(tokens.as[String]).as[JsArray].value.map(_.as[String])
//      println(stoks)
//      ws.url("http://localhost:5001/emo/extract").post(rs).map{ response =>
//        println(response.body)
//      }
      println(ec.execute(stoks))
    }
    Ok(views.html.index())
  }
  def getEmoFromTokens = Action.async { implicit request: Request[AnyContent] =>
     val emodel = request.body.asJson.get
     val tokens = (emodel \ "tokens" ).get
     val stoks = Json.parse(tokens.as[String]).as[JsArray].value.map(_.as[String])
     val futureString: Future[String] = scala.concurrent.Future {
          ec.execute(stoks)
     }
     futureString.map(s => Ok(s))
  }
  def getEntropy = Action.async { implicit request: Request[AnyContent] =>
    val futureString: Future[String] = scala.concurrent.Future {
       val emodel = request.body.asJson.get
       val emotions = (emodel \ "emotions" ).get
       ec.Entropy(emotions)
    }
    futureString.map(s => Ok("Got result: " + s))
  }
}
