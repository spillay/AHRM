package com.dsleng.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props,PoisonPill }
import com.dsleng.akka.pattern.ReaperWatched
import com.dsleng.akka.pattern.Reaper
import java.io.File
import com.dsleng.readers.MimeHandler
import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import scala.collection.mutable.ArrayBuffer
import akka.stream.scaladsl._
import org.apache.spark.SparkContext
//import com.dsleng.emo.EmoSimple 
import org.apache.spark.sql.{SparkSession}

object Simple {
  def props: Props = Props[Simple]
}
// (implicit sparkContext: SparkContext)
class Simple extends Actor with ActorLogging {
  import Simple._
  def appName: String = "Emotion Actors"
  def master: String = "local[*]"
  lazy val spark: SparkSession = SparkSession
      .builder
      .appName(appName)
      .master(master)
      .config("spark.eventLog.enabled", "true")
      .config("spark.eventLog.dir", "file:/usr/local/spark/history/")
      .config("spark.sql.codegen.wholeStage",false)
      .config("spark.sql.codegen",true)
      .config("spark.driver.memory","5g")
      //.config("spark.sql.parquet.filterPushdown","true")
      .getOrCreate()
      
   spark.sparkContext.setLogLevel("ERROR")
  def receive = {
    case "process" =>
       log.info("Processing instruction received (from " + sender() + "): ")
       
   }
}