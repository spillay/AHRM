package com.dsleng.uploadengine

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import com.dsleng.readers.MimeHandler
import com.dsleng.email.SimpleEmailModel
import java.io.File
import play.api.libs.json._
import scala.concurrent.{Await}
import scala.concurrent.duration.{Duration,TimeUnit}
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper

import com.dsleng.actor.EmailCtl
import com.dsleng.actor.Reader
import com.dsleng.actor.Reader.FileCtl
import com.dsleng.actor.Simple

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

/*
object Email {
  def props: Props = Props[Email]
}

class Email extends Actor with ActorLogging with ReaperWatched {
  import Email._

  def receive = {
    case "receive" =>
      log.info("Greeting received (from " + sender() + "): ")
      self ! "self"
    case "self" =>
      log.info("Self received (from " + sender() + "): ")
    case EmailCtl(model,emotion) =>
      log.info("Model received (from " + sender() + "): ")
      
  }
  //override def preStart(): Unit = {println("Starting Emotion")}
  override def postStop(): Unit = {println("Stopping Emotion")}
}

object Emotion {
  def props: Props = Props[Emotion]
}

class Emotion extends Actor with ActorLogging with ReaperWatched {
  import Emotion._

  def receive = {
    case "receive" =>
      log.info("Greeting received (from " + sender() + "): ")
      self ! "self"
    case "self" =>
      log.info("Self received (from " + sender() + "): ")
    case EmailCtl(model,emotion) =>
      log.info("Model received (from " + sender() + "): ")
      
  }
  //override def preStart(): Unit = {println("Starting Emotion")}
  override def postStop(): Unit = {println("Stopping Emotion")}
}

object EmailReader {
  //def props: Props = Props[EmailReader]
  def props(emotion: ActorRef): Props = Props(new EmailReader(emotion))
  val mhandle = new MimeHandler()
}

class EmailReader(emotion: ActorRef) extends Actor with ActorLogging with ReaperWatched {
  import EmailReader._

  def receive = {
    case "receive" =>
      log.info("Greeting received (from " + sender() + "): ")
      self ! "self"
    case "self" =>
      log.info("Self received (from " + sender() + "): ")
    case "process" =>
      log.info("Process received (from " + sender() + "): ")
      val f = new File("/Data/enron/maildir/kean-s/inbox/62.")
      val model = mhandle.processSimpleModel(f)
      val email = context.actorOf(Email.props,"Email")
      email ! new EmailCtl(model,"")
      println(email.path)
      emotion ! PoisonPill
      self ! PoisonPill
    case EmailCtl(model,emoStr) =>
      log.info("Message received (from " + sender() + "): " + model)
      emotion ! new EmailCtl(model,emoStr)
      
      
  }
  //override def preStart(): Unit = {println("Starting EmailReader")}
  override def postStop(): Unit = {println("Stopping EmailReader")}
}

*/
class Main {
  
}

object Main extends App {
  println("Starting UploadEngine...")
  val system: ActorSystem = ActorSystem("UploadEngine")
  system.registerOnTermination {
    println("ActorSystem terminated")
  }
  System.setProperty("hadoop.home.dir", "/");
  
  val reaperActor = system.actorOf(Props(new Reaper()), name=Reaper.name)
  
  //val reader: ActorRef = system.actorOf(Reader.props, "Reader")
  val simple: ActorRef = system.actorOf(Simple.props, "Simple")
  
  //reader ! "process"
  simple ! "process"
 
  
  Await.ready(system.whenTerminated, Duration.Inf)
  //system.c
}
//reader ! new FileCtl("/Data/enron/maildir/kean-s/inbox/62.")
 //reader ! PoisonPill
//  val emotion: ActorRef = system.actorOf(Emotion.props, "emotion")
//  val reader: ActorRef = system.actorOf(EmailReader.props(emotion), "emailReader")
//  println(emotion.path)
//  println(reader.path)
//  // If invoked from an instance that is not an Actor the sender will be deadLetters actor reference by default.
//  reader ! "process"
  