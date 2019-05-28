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
import play.api.libs.ws.StandaloneWSResponse

import scala.concurrent.{ Await, Future, future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

class EMOAnalysis  {
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
    //Await.ready(futureResponse, Duration.Inf)
    while(!futureResponse.isCompleted) {
      Thread.sleep(100)
    }
    return res
  }
  def processTokens(toks: String): String = {
    println(toks)
    val data = Json.parse(toks)
    var res = "none"
    val futureResponse = wsClient.url("http://localhost:9000/emo").post(data)
    futureResponse.onComplete {
      case Success(result) => {
                              println(s"result = $result");
                             
                              }
      case Failure(e)      => e.printStackTrace
    }
    futureResponse.map(resp=>{
      res = resp.body[String]
    })
    //Await.ready(futureResponse, Duration.Inf)
    while(!futureResponse.isCompleted) {
      println("waiting")
      Thread.sleep(100)
      println("end waiting")
    }
    return res
  }
 def processNEW(email: String): String = {
    var res = "none"
    val data = Json.obj(
    "text" -> email
    )
    val futureResponse = wsClient.url("http://localhost:5001/emo/tokens").post(data)
    futureResponse.onComplete {
      case Success(result) => {
                              println(s"result = $result");
                             
                              }
      case Failure(e)      => e.printStackTrace
    }
    futureResponse.map(resp=>{
      res = resp.body[String]
    })
    
    while(!futureResponse.isCompleted) {
      Thread.sleep(30)
    }
    return res
  }
 def getTokens(email: String): Future[StandaloneWSResponse] = {
   val data = Json.obj(
    "text" -> email
    )
    return wsClient.url("http://localhost:5001/emo/tokens").post(data)
 }
 def close(){
    Await.result(system.terminate(), 10.seconds)
 }
}

object EMOAnalysis { 
  def main(args: Array[String]): Unit = {
    val email = "i sure hope we make good use of the good news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor"
    val em = new EMOAnalysis()
    //val res = em.processNEW("i sure hope we make good use of the good news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor")
    //println(em.processTokens(res))
    em.getTokens(email).onComplete {
      case Success(result) => {
                              println(s"result = $result");
                             
                              }
      case Failure(e)      => e.printStackTrace
    }
    em.close()
  }
}