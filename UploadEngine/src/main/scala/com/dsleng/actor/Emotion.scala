package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import akka.util.ByteString
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper

import scala.concurrent.{Await, Future,Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.stream.ActorMaterializer

import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import com.dsleng.nlp.SimplePL
import scala.collection.JavaConversions._
import akka.stream.{ OverflowStrategy, QueueOfferResult }
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.util.Try
import com.dsleng.emo.helper._
import com.dsleng.emo.helper.CtlJsonImplicits._
import com.dsleng.email.SimpleEmailExt
import spray.json._

object Emotion {
  def props: Props = Props[Emotion]
}

class Emotion extends Actor with ActorLogging with ReaperWatched {
  import Emotion._
  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)

  
  def receive = {
    case TokenCtl(model,tokens) =>
      println("in emotion tokenctl")
      log.info("Processing tokens received (from " + sender() + "): " + tokens)  
      val origSender = sender
      val js = (new TokenCtl(model,tokens)).toJson
      
      //val js = tokens.toJson //Json.toJson(tokens)
      
      val requestEntity = HttpEntity(MediaTypes.`application/json`, js.toString())
      val req = HttpRequest(
              method = HttpMethods.POST,
              //uri = "/emo",
              uri = "http://localhost:9011/emo",
              entity = requestEntity
      )
      println("before firing")
      http.singleRequest(req).pipeTo(self)
//      val res = "not yet"
//     
     case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        println("Got response, body: " + body.utf8String)
        val output = body.utf8String
        val eec = output.parseJson.convertTo[EmoEmailCtl]
        log.debug("Got EmoEmailCtl")
        println("after eec",eec.emotion)
        val er = eec.emotion.parseJson.convertTo[EmoRes]
        println("before simpleemailext")
        val model = new SimpleEmailExt(
              eec.model.fileName,
              eec.model.model,
              "none"
            )
        model.prime = er.prime.toJson.toString()
        model.emotions = er.emotions.toJson.toString()
       
        val store = context.actorSelection("akka://UploadEngine/user/Reader/Store")
        store ! new EmoEmailCtl(model,"")  
      }
    case resp @ HttpResponse(code, _, _, _) =>
      println("Request failed, response code: " + code)
      resp.discardEntityBytes()
      
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}