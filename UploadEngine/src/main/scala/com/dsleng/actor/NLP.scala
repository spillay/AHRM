package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import akka.stream.ActorMaterializer

import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import com.dsleng.nlp.SimplePL
import scala.collection.JavaConversions._
import com.dsleng.emo.helper.{TokenCtl,EmailCtl,TokenStrCtl}
import spray.json._
import spray.json.DefaultJsonProtocol._
 //   ws.url("http://localhost:5001/emo/tokens").post(js).map{response =>

object NLP {
  def props: Props = Props[NLP]
}

class NLP extends Actor with ActorLogging with ReaperWatched {
  import NLP._
  
  //implicit val materializer = ActorMaterializer()
  //implicit val executionContext = context.system.dispatcher
  //implicit val system = context.system
  val simplePL = new SimplePL(SimplePL.nltk_sw,true)
  
  def receive = {
    case "start" =>
      log.debug("--------------------------------------Processing start instruction received (from " + sender() + "): ")
      val reader = context.actorSelection("akka://UploadEngine/user/Reader")
      reader ! "start"
    case EmailCtl(model) =>
      log.debug("Message received (from " + sender() + "): ++++++++++++++++++++++++++++++++++++++++++++++++++++++")
       
      var msg = model.model.textContent
//      if (msg.length() > 2000){
//        msg = msg.substring(0, 2000)
//      }
      log.info(model.fileName + " length: " + msg.length())
      val res = simplePL.processSentence(msg)
      log.debug(res)
      val obj = res.parseJson //Json.parse(res)
      val jr = obj.convertTo[Map[String, List[String]]]
      val toks = jr("tokensSW")
      
      //val reader = context.actorSelection("akka://UploadEngine/user/Reader")
      //reader ! "complete"
      
      val emotion = context.actorSelection("akka://UploadEngine/user/Reader/Emotion")
      emotion ! new TokenCtl(model,new TokenStrCtl(toks))
      
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}