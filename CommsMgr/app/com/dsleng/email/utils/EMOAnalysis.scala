package com.dsleng.email.utils
import play.api.libs.json._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import play.api.libs.ws.ahc._
import play.api.mvc.Result
import play.api.mvc.ResponseHeader
import play.api.http.HttpEntity
import play.api.libs.iteratee.Enumerator

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.{ Await, Future, future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

class EMOAnalysis extends WSHandler {
  import DefaultBodyReadables._
  import scala.concurrent.ExecutionContext.Implicits._
  implicit val system = ActorSystem()
  system.registerOnTermination {
    System.exit(0)
  }
  implicit val materializer = ActorMaterializer()
  val wsClient = StandaloneAhcWSClient()
  def processEMO(email: String): String = {
    val data = Json.obj(
    "text" -> email
    )
    val futureResponse = wsClient.url("http://localhost:5000/emo/get").post(data)
    var res = "none"
    futureResponse.onComplete {
      case Success(result) => {
                              println(s"result = $result");
                             
                              }
      case Failure(e)      => e.printStackTrace
    }
    futureResponse.map(resp=>{res = resp.body[String]})
    while(!futureResponse.isCompleted) {
      Thread.sleep(100)
    }
    return res
  }
}

object EMOAnalysis { 
  def main(args: Array[String]): Unit = {
    val em = new EMOAnalysis()
    println(em.processEMO("i sure hope we make good use of the bad news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor"))
  }
}