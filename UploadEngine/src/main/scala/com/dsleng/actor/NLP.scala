package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.stream.ActorMaterializer

import akka.http.scaladsl.model._
import akka.http.scaladsl.Http

import scala.collection.JavaConversions._
import com.dsleng.model.{TokenCtl,EmailCtl,TokenStrCtl,NLPDataCtl,TokenDataCtl}
import com.dsleng.email.SimpleEmailExt
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.dsleng.email.ModelJsonImplicits._
import com.dsleng.model.CtlJsonImplicits._
import akka.util.ByteString


object NLP {
  def props: Props = Props[NLP]
}

class NLP extends Actor with ActorLogging with ReaperWatched {
  import NLP._
  import akka.pattern.pipe
  import context.dispatcher
  
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)
  
  
  def receive = {
    case "start" =>
      log.debug("--------------------------------------Processing start instruction received (from " + sender() + "): ")
      val reader = context.actorSelection("akka://UploadEngine/user/Reader")
      reader ! "start"
    case EmailCtl(model) =>
      log.debug("Message received (from " + sender() + "): ++++++++++++++++++++++++++++++++++++++++++++++++++++++")
       
      var msg = model.toJson
//      if (msg.length() > 2000){
//        msg = msg.substring(0, 2000)
//      }
      val requestEntity = HttpEntity(MediaTypes.`application/json`, msg.toString())
      val req = HttpRequest(
              method = HttpMethods.POST,
              //uri = "/emo",
              uri = "http://localhost:9012/nlp",
              entity = requestEntity
      )
      println("before firing")
      http.singleRequest(req).pipeTo(self)
      
      
      
      
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        println("Got response, body: " + body.utf8String)
        val output = body.utf8String
        val value = output.parseJson
        println(value)
        val model = value.asJsObject.getFields("model")(0).convertTo[SimpleEmailExt]
        val nlp = value.asJsObject.getFields("nlp")(0).convertTo[NLPDataCtl]
        println("at nlp",nlp.tokens)
        val emotion = context.actorSelection("akka://UploadEngine/user/Reader/Emotion")
        emotion ! new TokenDataCtl(model,nlp)
      }
    case resp @ HttpResponse(code, _, _, _) =>
      println("Request failed, response code: " + code)
      resp.discardEntityBytes()
      
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}