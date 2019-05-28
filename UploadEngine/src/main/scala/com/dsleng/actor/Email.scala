package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper


object Email {
  def props: Props = Props[Email]
}

class Email extends Actor with ActorLogging with ReaperWatched {
  import Email._
  val emotion = context.actorOf(Emotion.props,"Emotion")
  
  def receive = {
    case "process" =>
      log.info("Processing instruction received (from " + sender() + "): ")
    case EmailCtl(model) =>
      log.info("Message received (from " + sender() + "): " + model)  
      emotion ! new EmailCtl(model)
    case EmoEmailCtl(model,value) =>
      log.info("Done received (from " + sender() + "): " + model)
      log.info(value)
      //self ! PoisonPill
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}