package com.dsleng.server.nlp

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.http.scaladsl.model.{ResponseEntity,HttpResponse}
import akka.annotation.ApiMayChange
import akka.http.scaladsl.model.StatusCodes


import spray.json._
import spray.json.DefaultJsonProtocol._
import com.dsleng.nlp.SimplePL
import com.dsleng.email.ModelJsonImplicits._
import com.dsleng.email.SimpleEmailExt
import com.dsleng.model.{EmoEmailCtl,NLPDataCtl,TokenDataCtl}
import com.dsleng.model.CtlJsonImplicits._

@ApiMayChange
object WebServer extends HttpApp {

  val simplePL = new SimplePL(SimplePL.nltk_sw,true)
  
  override def routes: Route =
    path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
    } ~
    path("test") {
      parameter('id.as[Int]) { id =>
        post { 
          complete {
            s"CustId: ${id}"
          }
        }
      }
    } ~
    path("nlp") { 
      post {
        entity(as[String]){ param =>{
          //println(param)
          val model = param.parseJson.convertTo[SimpleEmailExt]
          val res = simplePL.processSentence(model.model.textContent)
          val nlp = res.parseJson.convertTo[NLPDataCtl]
          val result = new TokenDataCtl(model,nlp)
          //val result = "not yet"
          val resp: ResponseEntity = HttpEntity(ContentTypes.`application/json`,result.toJson.toString())
          complete(HttpResponse(StatusCodes.OK, entity = resp))
        }
       }
      }
    }
  
}

