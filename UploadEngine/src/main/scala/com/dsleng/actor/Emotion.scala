package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.stream.ActorMaterializer

import play.api.libs.json._
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
 
 //   ws.url("http://localhost:5001/emo/tokens").post(js).map{response =>

object Emotion {
  def props: Props = Props[Emotion]
}

class Emotion extends Actor with ActorLogging with ReaperWatched {
  import Emotion._
  
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher
  implicit val system = context.system
  
  def receive = {
    case "process" =>
      log.info("Processing instruction received (from " + sender() + "): ")
    case TokenCtl(model,tokens) =>
      log.info("Processing tokens received (from " + sender() + "): " + tokens)  
      val origSender = sender
      val js = Json.parse(tokens)
      val tks = js \ "tokens"
      log.info(tks.as[String])
      val requestEntity = HttpEntity(MediaTypes.`application/json`, js.toString())
      val responseFuture: Future[HttpResponse] = Http().singleRequest(
          HttpRequest(
              method = HttpMethods.POST,
              uri = "http://localhost:9000/emo2",
              entity = requestEntity
          ))
      responseFuture.onComplete {
        case Success(value) => {
          println(s"Got the callback, meaning = $value")
          println(sender())
          val HttpResponse(statusCodes, headers, entity, _) = value
          println(statusCodes)
          Unmarshal(entity).to[String].map(res=>{
             println(res)
             val email = context.actorSelection("akka://UploadEngine/user/Reader/Email")
             model.emotions = res
             email ! new EmoEmailCtl(model,res)
          })
          
        }
        case Failure(e) => e.printStackTrace
      }
      
    case EmailCtl(model) =>
      log.info("Message received (from " + sender() + "): " + model.model.textContent)
      val data = new TextCtl(model.model.textContent)
      val js = Json.toJson(data)
      val origSender = sender
      

      val requestEntity = HttpEntity(MediaTypes.`application/json`, js.toString)
      val responseFuture: Future[HttpResponse] = Http().singleRequest(
          HttpRequest(
              method = HttpMethods.POST,
              uri = "http://localhost:5001/emo/tokens",
              entity = requestEntity
          ))
      responseFuture.onComplete {
        case Success(value) => {
          println(s"Got the callback, meaning = $value")
          println(sender())
          val HttpResponse(statusCodes, headers, entity, _) = value
          println(statusCodes)
          Unmarshal(entity).to[String].map(res=>{
            self ! new TokenCtl(model,res)
          })
          
        }
        case Failure(e) => e.printStackTrace
      }
      
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}