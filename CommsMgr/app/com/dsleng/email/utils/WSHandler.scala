package com.dsleng.email.utils
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.Future
import play.api.http.Status

trait WSHandler {
  import scala.concurrent.ExecutionContext.Implicits._
   def processWS(email: String): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val wsClient = AhcWSClient()

    call(wsClient,email)
      .andThen { case _ => wsClient.close() }
      .andThen { case _ => system.terminate() }
  }

  def call(wsClient: WSClient,email: String): Future[Unit] = {
    wsClient.url("http://localhost:9000/analyze")
    .addHttpHeaders("Content-Type" -> "application/json")
    .post(email)
    .map { response =>
      val statusText: String = response.statusText
      println(s"Got a response $statusText")
      if ( response.status == Status.OK){
        println(response.body[String])
      }
    }
  }
}