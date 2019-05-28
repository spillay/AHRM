package com.dsleng.uploadengine

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
//import com.dsleng.readers.MimeHandler
import java.io.File
import play.api.libs.json._

case class Email(name: String,from: String)

object Email {
  implicit val emailWrites = Json.writes[Email]
  
//  implicit val emailWrites = new Writes[Email] {
//    def writes(a: Email) = Json.obj(
//      "Address" -> a.name)
//  }
}

object Emotion {
  def props: Props = Props[Emotion]
}

class Emotion extends Actor with ActorLogging {
  //import Printer._

  def receive = {
    case "receive" =>
      log.info("Greeting received (from " + sender() + "): ")
      self ! "self"
    case "self" =>
      log.info("Self received (from " + sender() + "): ")
  }
  override def preStart(): Unit = {println("Starting Emotion")}
  override def postStop(): Unit = {println("Stopping Emotion")}
}

object EmailReader {
  def props: Props = Props[EmailReader]
}

class EmailReader extends Actor with ActorLogging {
  //val mhandle = new MimeHandler()

  def receive = {
    case "receive" =>
      log.info("Greeting received (from " + sender() + "): ")
      self ! "self"
    case "self" =>
      log.info("Self received (from " + sender() + "): ")
      
  }
  override def preStart(): Unit = {println("Starting EmailReader")}
  override def postStop(): Unit = {println("Stopping EmailReader")}
}


class Main {
  
}

object Main extends App {
  println("Starting UploadEngine...")
  val system: ActorSystem = ActorSystem("UploadEngine")
  
  val reader: ActorRef = system.actorOf(EmailReader.props, "emailReader")
  // If invoked from an instance that is not an Actor the sender will be deadLetters actor reference by default.
  reader ! "receive"
  
//  val mhandle = new MimeHandler()
//  val f = new File("/Data/enron/maildir/kean-s/inbox/9.")
//  val res = mhandle.processSimpleModel(f)
//  println(res.from)
  var em = new Email("name","from")
  println(Json.toJson(em))
  
  system.terminate()
}