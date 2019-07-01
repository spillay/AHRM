package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper
import java.io.File
import com.dsleng.readers.MimeHandler
import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import scala.collection.mutable.ArrayBuffer
import akka.stream.scaladsl._

import util.control.Breaks._
import com.dsleng.emo.helper.{EmailCtl}
object Reader {
  def props: Props = Props[Reader]
  final case class FileCtl(file: String)
  val mhandle = new MimeHandler()
  
}

class Reader extends Actor with ActorLogging with ReaperWatched {
  import Reader._
  val email = context.actorOf(Email.props,"Email")
  val emotion = context.actorOf(Emotion.props,"Emotion")
  val nlp = context.actorOf(NLP.props,"NLP")
  val store = context.actorOf(Store.props,"Store")
  var cnt = 0
  
  def receive = {
    case "process" =>
       log.info("Processing instruction received (from " + sender() + "): ")
       nlp ! "start"
    case "start" =>
      log.debug("Reader Starting")
      self ! new FileCtl("/Data/enron/maildir/kean-s/inbox/")
    case "complete" =>
      cnt = cnt + 1
      println("Counter " + cnt + " -------------------------------------------------------")
    case FileCtl(file) =>
      log.info("Processing file instruction received (from " + sender() + "): ")
     // val f = new File(file)
      
      val ubox = new File("/Data/enron/maildir/kean-s/inbox/")
      //val ubox = new File("/Data/enron/test/")
      
      val files = ubox.listFiles().filter(f=>(!f.isDirectory()))
      // Fix Sink
      val s1 = Source(files.toList).map(f=>{
        val model = mhandle.processSimpleModel(f)
        var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, "none", department = "", product = ArrayBuffer[String]().toArray)
        new EmailCtl(smodel)
      })//.to(sink)
      
      var fileCnt = 0
      ubox.listFiles().foreach(f=>{
        if (!f.isDirectory()){
          log.info("Processing from Reader: {}",f.getAbsoluteFile())
          val model = mhandle.processSimpleModel(f)
          var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, "none", department = "", product = ArrayBuffer[String]().toArray)
          fileCnt = fileCnt + 1
          println("FileCounter " + fileCnt + " : " + cnt + "=====================================================")
          email ! new EmailCtl(smodel)
          break
        }
      })
  }
  override def postStop(): Unit = {println("Stopping Emotion")}
}