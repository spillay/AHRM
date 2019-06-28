package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper

import scala.concurrent.{Await, Future,Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.stream.ActorMaterializer

import play.api.libs.json._
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import com.dsleng.nlp.SimplePL
import scala.collection.JavaConversions._
import akka.stream.{ OverflowStrategy, QueueOfferResult }
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.util.Try
import com.dsleng.emo.helper.{TokenCtl,EmoEmailCtl}
 //   ws.url("http://localhost:5001/emo/tokens").post(js).map{response =>
import com.dsleng.emo.helper.SPJsonImplicits._
import spray.json._

object Emotion2 {
  def props: Props = Props[Emotion2]
}

class Emotion2 extends Actor with ActorLogging with ReaperWatched {
  import Emotion2._
  
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher
  implicit val system = context.system
  
  val poolClientFlow1 = Http().cachedHostConnectionPool[Int]("localhost", 9000)
  val poolClientFlow = Http().cachedHostConnectionPool[Promise[HttpResponse]]("localhost",9000)
  
  val QueueSize = 30
  val queue = Source.queue[(HttpRequest, Promise[HttpResponse])](QueueSize, OverflowStrategy.backpressure)
  .via(poolClientFlow)
  .toMat(Sink.foreach({
    case ((Success(resp), p)) => p.success(resp)
    case ((Failure(e), p))    => p.failure(e)
  }))(Keep.left)
  .run()
  
  def queueRequest(request: HttpRequest): Future[HttpResponse] = {
    val responsePromise = Promise[HttpResponse]()
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued    => responsePromise.future
      case QueueOfferResult.Dropped     => Future.failed(new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed => Future.failed(new RuntimeException("Queue was closed (pool shut down) while running the request. Try again later."))
    }
  }
  //Source.fromPublisher(publisher)
  def receive = {
    case TokenCtl(model,tokens) =>
      log.info("Processing tokens received (from " + sender() + "): " + tokens)  
      val origSender = sender
      val js = tokens.toJson
      
      val email = context.actorSelection("akka://UploadEngine/user/Reader/Email")
      email ! new EmoEmailCtl(model,"not implemented emotion")
      
  }
//    case TokenCtl(model,tokens) =>
//      log.info("Processing tokens received (from " + sender() + "): " + tokens)  
//      val origSender = sender
//      val js = Json.toJson(tokens)
//      
//      val requestEntity = HttpEntity(MediaTypes.`application/json`, js.toString())
//      val req = HttpRequest(
//              method = HttpMethods.POST,
//              uri = "/emo2",
//              //uri = "http://localhost:9000/emo2",
//              entity = requestEntity
//      )
//      val responseFuture: Future[HttpResponse] = queueRequest(req)
//      responseFuture.onComplete {
//        case Success(value) => {
//          log.info(s"Got the callback, meaning = $value")
//          val HttpResponse(statusCodes, headers, entity, _) = value
//          log.debug(statusCodes.toString())
//          Unmarshal(entity).to[String].map(res=>{
//             log.info(res)
//             val email = context.actorSelection("akka://UploadEngine/user/Reader/Email")
//             model.emotions = res
//             email ! new EmoEmailCtl(model,res)
//          })
//          
//        }
//        case Failure(e) => e.printStackTrace
//      }
      
//    case TokenCtl(model,tokens) =>
//      log.debug("Processing tokens received (from " + sender() + "): " + tokens)  
//      val origSender = sender
//      val js = Json.toJson(tokens)
//      
//      //val tks = js \ "tokens"
//      //log.info(tks.as[String])
//      log.info(js.toString())
//      val requestEntity = HttpEntity(MediaTypes.`application/json`, js.toString())
//      val responseFuture: Future[HttpResponse] = Http().singleRequest(
//          HttpRequest(
//              method = HttpMethods.POST,
//              uri = "http://localhost:9000/emo2",
//              entity = requestEntity
//          ))
//      responseFuture.onComplete {
//        case Success(value) => {
//          log.debug(s"Got the callback, meaning = $value")
//          val HttpResponse(statusCodes, headers, entity, _) = value
//          log.debug(statusCodes.toString())
//          Unmarshal(entity).to[String].map(res=>{
//             log.debug(res)
//             val email = context.actorSelection("akka://UploadEngine/user/Reader/Email")
//             model.emotions = res
//             email ! new EmoEmailCtl(model,res)
//          })
//          
//        }
//        case Failure(e) => e.printStackTrace
//      }
        

  override def postStop(): Unit = {println("Stopping Emotion")}
}