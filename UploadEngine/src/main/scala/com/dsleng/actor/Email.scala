package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper


object Email {
  def props: Props = Props[Email]
}

class Email extends Actor with ActorLogging with ReaperWatched {
  import Email._
  
  
  def receive = {
    case "process" =>
      log.debug("Processing instruction received (from " + sender() + "): ")
    case EmailCtl(model) =>
      log.debug("Message received (from " + sender() + "): " + model.fileName)  
      val nlp = context.actorSelection("akka://UploadEngine/user/Reader/NLP")
      nlp ! new EmailCtl(model)
    case EmoEmailCtl(model,value) =>
      log.info("Done received (from " + sender() + "): " + model.fileName)
      log.info(value)
      val reader = context.actorSelection("akka://UploadEngine/user/Reader")
      reader ! "complete"
      //self ! PoisonPill
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}