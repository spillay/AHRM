package com.dsleng.email

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

import scala.concurrent.{Await, Future, future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class EmailAnalysis {
  import DefaultBodyReadables._
  import scala.concurrent.ExecutionContext.Implicits._
  implicit val system = ActorSystem()
    system.registerOnTermination {
      System.exit(0)
    }
  implicit val materializer = ActorMaterializer()
  /*
  implicit def Response2Result(response: Future[WSResponse]): Future[Result] = {
    response map {
      response =>
        val headers = response.allHeaders map {
          h => (h._1, h._2.head)
        }
        
        val entity = HttpEntity.Strict(response.body.getBytes)
        Result(ResponseHeader(response.status, headers), HttpEntity.Strict(response.body.getBytes()))
    }
  }
  * 
  */
  def processGender(msg: String): String ={
    

    // Create the standalone WS client
    // no argument defaults to a AhcWSClientConfig created from
    // "AhcWSClientConfigFactory.forConfig(ConfigFactory.load, this.getClass.getClassLoader)"
    val wsClient = StandaloneAhcWSClient()
    val nmsg = "The cat sat on the mat eating cheese"
    val futureResponse = 
      wsClient.url("http://app.jaasuz.com:8000/init/gender_service/call/run/analyze_gender")
    .addQueryStringParameters("newtext" -> nmsg)
    .get()
   
   //Await.ready(futureResponse, Duration.Inf)
   var res = "none"
   futureResponse.onComplete {
     case Success(result) => println(s"result = $result")
     case Failure(e) => e.printStackTrace
   }
   Await.ready(futureResponse, Duration.Inf)
   /*
   futureResponse.map{
      case resp: WSResponse => {
        println("Status " + resp.status)
        if (resp.status == 200){
          wsClient.close()
          //system.terminate()
          res = resp.body[String].toString()
          println(res)
        }
      }
      case _ => {
        println("Failed")
        wsClient.close()
        //system.terminate()
        res = "Failed"
      }
      
    }
   */
    return res
   
    /*
    call(wsClient)
      .andThen { case _ => wsClient.close() }
      .andThen { case _ => system.terminate() }
      *
      */
   
  }
  def call(wsClient: StandaloneWSClient): Future[Unit] = {
    wsClient.url("http://app.jaasuz.com:8000/init/gender_service/call/run/analyze_gender")
    .addQueryStringParameters("newtext" -> "The cat sat on the mat")
    .get().map { response ⇒
      val statusText: String = response.statusText
      val body = response.body[String]
      println(s"Got a response $statusText")
      println(body)
    }
  }
}