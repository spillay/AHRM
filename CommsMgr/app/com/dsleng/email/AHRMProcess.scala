package com.dsleng.email

import com.dsleng.email.utils.FileStore
import com.dsleng.email.utils.MetaInfo
import com.dsleng.email.utils.TimeMgr
import com.dsleng.email.utils.Batch
import com.google.common.io.Files
import java.io.File
import play.api.libs.json._
import com.dsleng.email.utils.EMOAnalysis
import com.dsleng.email.utils.GenMetaData
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
//import com.dsleng.email.{EmailModel,SimpleEmailExt,SimpleEmailModel,SenderInfo,EmailHeader}

import scala.collection.mutable.ArrayBuffer

object AHRMProcess {
  val usage = """
    Usage: ARHMProcess --host ipaddress
  """
 
  def sendSimulate(host: String,index: String){
    val sim = new Simulate()
    val feeder = new ESFeeder(host)
    sim.getAll().foreach(e=>{
      feeder.addDocumentA(e, index)
    })
    feeder.close()
  }
  def sendDoc(host: String,index: String){
    println("send document")
//    val emailModel = new EmailModel(
//      List.empty[EmailHeader],
//      ArrayBuffer[SenderInfo](),
//      "from@example.com",
//      "to@example.com",
//      "date",
//      "subject",
//      "no-content",
//      "no-html-content",
//      "dept",
//      ArrayBuffer[String]("ProductA").toArray
//    )
    var simpleModel = AEmailModel(
      "from@example.com",
      "to@example.com",
      "2019-04-12T12:00:00",
      "subject",
      "senderIP",
      "receiverIP",
      "no-content",
      "no-html-content",
    )
    val emotions = """
    {
      "ec":"{\"Contentment\": 1, \"Disgust\": 2, \"Anger\": 1}",
      "norm":"{\"Contentment\": 0.25, \"Disgust\": 0.5, \"Anger\": 0.25}",
      "prime":"\"Disgust\""
     }
     """
    val sEmail = new AEmailExt(
        "",
        simpleModel,
        "Operations",
        ArrayBuffer[String]("ProductA").toArray,
        emotions
    )
    println(sEmail.model.date.toString())
    val feeder = new ESFeeder(host)
    feeder.addDocumentA(sEmail, index)
    feeder.close()
    
  }
  def getDate(d: String): java.util.Date = {
    val form = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
    return form.parseDateTime(d).toDate()
  }
  def convertDate(datetime: String): String = {
    val dtf = DateTimeFormat.forPattern("MM-dd-yyyy'T'HH:mm:ss");
    val dt = dtf.parseDateTime(datetime)
    val form = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
    return dt.toString(form)
  }
  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      println(usage)
      System.exit(1)
    }
    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
      def isSwitch(s: String) = (s(0) == '-')
      // 'batch translates to Symbol("batch")
      list match {
        case Nil => map
        case "--index" :: value :: tail =>
          nextOption(map ++ Map('index -> value.toString), tail)
        case "--host" :: value :: tail =>
          nextOption(map ++ Map('host -> value.toString), tail)
        case _ => map
      }
    }
    val options = nextOption(Map(), arglist)
    println(options)
    val host: String = options.getOrElse('host, "").asInstanceOf[String]
    val index: String = options.getOrElse('index, "AHRM").asInstanceOf[String]
    println(host)
    println(index)
    //sendDoc(host,index)
    sendSimulate(host,index)
    println("Terminated !!!");
    //System.exit(0);
  }

}
