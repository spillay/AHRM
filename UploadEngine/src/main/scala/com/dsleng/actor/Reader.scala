package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper
import java.io.File
import com.dsleng.readers.MimeHandler
import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import scala.collection.mutable.ArrayBuffer

object Reader {
  def props: Props = Props[Reader]
  final case class FileCtl(file: String)
  val mhandle = new MimeHandler()
  
}

class Reader extends Actor with ActorLogging with ReaperWatched {
  import Reader._
  val email = context.actorOf(Email.props,"Email")
  
  def receive = {
    case "process" =>
       log.info("Processing instruction received (from " + sender() + "): ")
    case FileCtl(file) =>
      log.info("Processing file instruction received (from " + sender() + "): ")
     // val f = new File(file)
      
      //val ubox = new File("/Data/enron/maildir/kean-s/inbox/")
      val ubox = new File("/Data/enron/test/")
      ubox.listFiles().foreach(f=>{
        if (!f.isDirectory()){
          val model = mhandle.processSimpleModel(f)
          var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, "none", department = "", product = ArrayBuffer[String]().toArray)
          email ! new EmailCtl(smodel)
        }
      })
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}