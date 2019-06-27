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
//import com.dsleng.emo.EmoSimple 

object Emotion {
  def props: Props = Props[Emotion]
}

class Emotion extends Actor with ActorLogging with ReaperWatched {
  import Emotion._
  //val emo = new EmoSimple()
  
  def receive = {
    case TokenCtl(model,tokens) =>
      log.info("Processing tokens received (from " + sender() + "): " + tokens)  
      val origSender = sender
      val js = Json.toJson(tokens)
      
      
      //val res = emo.execute(tokens.tokens)
      val res = "not yet"
      val email = context.actorSelection("akka://UploadEngine/user/Reader/Email")
      email ! new EmoEmailCtl(model,res)  
      
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}