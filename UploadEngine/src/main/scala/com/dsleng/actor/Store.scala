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
import com.dsleng.emo.helper.{TokenCtl,EmailCtl,TokenStrCtl,EmoEmailCtl}
import spray.json._
import spray.json.DefaultJsonProtocol._
 //   ws.url("http://localhost:5001/emo/tokens").post(js).map{response =>

object Store {
  def props: Props = Props[Store]
}

class Store extends Actor with ActorLogging with ReaperWatched {
  import Store._
 
  
  def receive = {
    case EmoEmailCtl(model,res) =>
      log.debug("Message received (from " + sender() + "): ++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      println("store it")
    
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}