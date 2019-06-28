package com.dsleng.email


import javax.mail.Address
import scala.collection.mutable.ListBuffer
//import com.google.common.net.InetAddresses
import java.net.InetAddress
import com.dsleng.email.utils.IPTZ
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.net.InetAddress
import java.net.UnknownHostException

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.dsleng.model.utils.DateMarshalling._

case class RecipientInfo(Email: String, Department: String)
case class DummySenderInfo(Email: String, IP: String)
case class DummySenderInfoExt(Email: String, IP: String, senderGeoIP: GeoInfo, tz: String, products: Array[String])

case class Gender(gender: String, percentage: String)

case class Deception(indicator: String, level: String)

case class SenderInfo(fromip: String, fromhost: String, byip: String, byhost: String)

case class EmailHeader(name: String, value: String)
case class Location(lat: String, lon: String){
  def getlocation(): String ={
    var loc = "{"
    loc += "\"lat\" : " + lat + ","
    loc += "\"lon\" : " + lon
    loc += "}"
    return loc
  }
}

case class GeoInfo(ip: String, country_code: String, country_name: String,
                   region_code: String, region_name: String, city: String, 
                   zip_code: String, time_zone: String, latitude: String, 
                   longitude: String, metro_code: String)

case class Headers(theHeaders: List[EmailHeader])

case class AEmailModel(
  var from:              String = "",
  var to:                String = "",
  var date:              String = "",
  var subject:           String = "",
  var senderIP:          String = "",
  var receiverIP:          String = "",
  var textContent: String  = "", 
  var htmlContent: String = "",
  var senderLocation: Location = null,
  var receiverLocation: Location = null,
  var senderGeoInfo: GeoInfo = null,
  var receiverGeoInfo: GeoInfo = null) {
  
 
  this.senderIP = getIP(this.from)
  this.senderGeoInfo = getGeoLocation(this.senderIP);
  
  this.receiverIP = getIP(this.to)
  this.receiverGeoInfo = getGeoLocation(this.receiverIP);
  
  this.senderLocation = new Location(this.senderGeoInfo.latitude,this.senderGeoInfo.longitude)
  this.receiverLocation = new Location(this.receiverGeoInfo.latitude,this.receiverGeoInfo.longitude)
   
 
  def extractHost(from: String): String = {
    val pos = from.indexOf("@")
    var dm = from.substring(pos + 1, from.length())
    //println(dm)
    return dm
  }
  def getIP(from: String): String = {
    val host = extractHost(from)
    try {
      val ips = InetAddress.getAllByName(host)
      ips.foreach(f => {
        return f.getHostAddress()
      })
    } catch {
      case e: UnknownHostException => {
        //TODO: write this to a log file for later analysis
        //println("Unknown Host")
      }
    }
    return "0.0.0.0"
  }
  def getGeoLocation(ip: String): GeoInfo = {
    //var geo = new IPTZ()
    //return geo.getGeoInfo(ip);
    
    val geoinfo = new GeoInfo(
         "184.168.221.79",
          "US",
         "United States",
          "AZ",
          "Arizona",
          "Scottsdale",
          "85260",
          "America/Phoenix",
          "33.6013",
          "-111.8867",
          "753"
          )
    return geoinfo
  }
}

case class SimpleEmailModel(
  var allHeaders:  Headers,
  var textContent: String  = "", 
  var htmlContent: String = "",
  var from:              String = "",
  var to:                String = "",
  var date:              Date = null,
  var subject:           String = "",
  var senderIP:          String = "",
  var receiverIP:          String = "",
  var senderLocation: Location = null,
  var receiverLocation: Location = null,
  var senderGeoInfo: GeoInfo = null,
  var receiverGeoInfo: GeoInfo = null) {
  allHeaders.theHeaders.foreach(h => {
    ////println(h.name)
    if (h.name == "From") {
      //println(h.value)
      this.from = h.value
    }
    if (h.name == "To") {
      ////println(h.value)
      this.to = h.value
    }
    if (h.name == "Date") {
      ////println(h.value)
      var format = new SimpleDateFormat("E, d MMM YYYY hh:mm:ss ZZZZ (zzz)", Locale.ENGLISH);
      //var format = new SimpleDateFormat("E, d MMM YYYY", Locale.ENGLISH);
      val dte = format.parse(h.value)
      this.date = dte
    }
    if (h.name == "Subject") {
      ////println(h.value)
      this.subject = h.value
    }

  })
  removeOriginalMesg()
  this.senderIP = getIP(this.from)
  this.senderGeoInfo = getGeoLocation(this.senderIP);
  
  this.receiverIP = getIP(this.to)
  this.receiverGeoInfo = getGeoLocation(this.receiverIP);
  
  this.senderLocation = new Location(this.senderGeoInfo.latitude,this.senderGeoInfo.longitude)
  this.receiverLocation = new Location(this.receiverGeoInfo.latitude,this.receiverGeoInfo.longitude)
   
  def removeOriginalMesg() {
    val ch = "-----Original Message-----"
    var pos = this.textContent.indexOf(ch)
    ////println("position val " + pos.toDouble)
    if (pos != -1) {
      this.textContent = this.textContent.substring(0, pos)
    }
  }
  def extractHost(from: String): String = {
    val pos = from.indexOf("@")
    var dm = from.substring(pos + 1, from.length())
    ////println(dm)
    return dm
  }
  def getIP(from: String): String = {
    val host = extractHost(from)
    try {
      val ips = InetAddress.getAllByName(host)
      ips.foreach(f => {
        return f.getHostAddress()
      })
    } catch {
      case e: UnknownHostException => {
        //TODO: write this to a log file for later analysis
        //println("Unknown Host")
      }
    }
    return "0.0.0.0"
  }
  def getGeoLocation(ip: String): GeoInfo = {
    //var geo = new IPTZ()
    //return geo.getGeoInfo(ip);
    
     val geoinfo = new GeoInfo(
         "184.168.221.79",
          "US",
         "United States",
          "AZ",
          "Arizona",
          "Scottsdale",
          "85260",
          "America/Phoenix",
          "33.6013",
          "-111.8867",
          "753"
          )
    return geoinfo
  }
}

case class EmailModel(
  var allHeaders: List[EmailHeader],
  var senderIp:   List[SenderInfo], 
  //var senderIp:   scala.collection.mutable.ArrayBuffer[SenderInfo], 
  var from: String,
  var to: String, 
  var date: String, 
  var subject: String, 
  var textContent: String,
  var htmlContent: String, 
  var Department: String,
  var products:    Array[String] = null,
  var senderGeoIP: GeoInfo       = null) {
  if (senderIp.isEmpty) {
    //println("processing sender")
    allHeaders.foreach(h => {
      //println(h.name)
      if (h.name == "Received") {
        ////println(h.value)
        //senderIp += extract(h.value)
        senderIp = senderIp.::(extract(h.value))
      }
      if (h.name == "From") {
        //println(h.value)
      }
      if (h.name == "X-From") {
        //println(h.value)
      }
    })
  }
  if (date == "") {
    allHeaders.foreach(h => {
      if (h.name == "Delivery-date") {
        date = h.value
      }
      if (h.name == "Date") {
        //println(h.value)
        date = h.value
      }
    })
  }
  def convertDate(datetime: String): String = {
    val dtf = DateTimeFormat.forPattern("MM-dd-yyyy'T'HH:mm:ss");
    val dt = dtf.parseDateTime(datetime)
    val form = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
    return dt.toString(form)
  }
  def Initialize() {

    this.date = ""
    this.to = ""
    this.Department = ""
    this.textContent = ""
    this.htmlContent = ""
    this.subject = ""
    //this.products = ""
    this.from = ""
    // val si = new SenderInfo(send.IP,"",send.IP,"")
    // senderIp += si
    //this.senderGeoIP = send.senderGeoIP

  }
  def dummyUpdate(metaData: com.dsleng.email.utils.MetaInfo, datetime: String) {
    val rec = metaData.getRecipient()
    val send = metaData.getSenderBatch(datetime)
    this.date = convertDate(datetime)
    this.to = rec.Email
    this.Department = rec.Department
    this.products = send.products
    //this.products.foreach(p=>{//println("emailmodel" + p)})
    this.from = send.Email
    val si = new SenderInfo(send.IP, "", send.IP, "")
    //senderIp += si
    senderIp = senderIp.::(si)
    this.senderGeoIP = send.senderGeoIP

  }
  def getIP(s: String): String = {
    val regex = """(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})""".r
    val fromip = regex.findFirstIn(s) match {
      case None     => ""
      case Some(ws) => ws
    }
    return fromip
  }
  def getHOST(s: String): String = {
    val regex = """[\S]*\.[\S]*\.[\S]*\.[\S]*\s""".r
    val fromhost = regex.findFirstIn(s) match {
      case None     => ""
      case Some(ws) => ws
    }
    return fromhost
  }
  def extract(s: String): SenderInfo = {
    var ns = s.replaceAll("[\\t\\n\\r]+", " ");
    ////println(ns)
    val from = """(from)(.*)(by)""".r.findFirstMatchIn(ns) match {
      case None     => ""
      case Some(ws) => ws
    }
    val by = """(by)(.*)(with)""".r.findFirstMatchIn(ns) match {
      case None     => ""
      case Some(ws) => ws
    }
    val ewith = """(with)(.*)(for)""".r.findFirstMatchIn(ns) match {
      case None     => ""
      case Some(ws) => ws
    }
    val efor = """(for).*""".r.findFirstMatchIn(ns) match {
      case None     => ""
      case Some(ws) => ws
    }
    // //println("from " + from)
    // //println("by " + by)
    // //println("with " + ewith)
    // //println("for " + efor)

    // Find ips or hosts in strings
    ////println("from ip " + this.getIP(from.toString()))
    ////println("from host " + this.getHOST(from.toString()))
    return new SenderInfo(this.getIP(from.toString()), this.getHOST(from.toString()), this.getIP(by.toString()), this.getHOST(by.toString()))
  }
}

case class EmailExt(var model: EmailModel, genderInfo: Gender, deception: Deception, var clientip: String, var location: Location = null, var timestamp: String = "") {
  model.senderIp.foreach(ip => {
    if (ip.fromip != "" && ip.fromip != "127.0.0.1" && ip.fromip != "0.0.0.0" && !InetAddress.getByName(ip.fromip).isSiteLocalAddress()) {
      clientip = ip.fromip
    }
  })
  // Temporary to clean up remove the content
  this.location = new Location(model.senderGeoIP.latitude, model.senderGeoIP.longitude)

  this.timestamp = model.date.toString()
  model.htmlContent = ""
  model.textContent = ""
  model.allHeaders = (new ListBuffer[EmailHeader]()).toList
  model.senderIp = new scala.collection.mutable.ArrayBuffer[SenderInfo]().toList

}
case class SimpleEmailExt(
    var fileName: String,
    var model: SimpleEmailModel, 
    var emotions: String,
    var prime: String="Unknown",
    var norm: String="",
    var ec: String="",
    var department: String ="Unassigned",
    product: Array[String] = Array[String]()) {
  println("Emotions " + emotions)
  if (emotions != "none"){ 
    //val res = emotions.parseJson
    val obj = emotions.parseJson //Json.parse(emotions)
    val res = obj.convertTo[Map[String, String]]
    
    this.prime = res("prime")
    //this.prime = obj.\("prime").as[String]
    this.prime = this.prime.replaceAll("\"", "")
    
    var n = res("norm")
    //var n = obj.\("norm")
    
    
    this.norm = res("norm")
    //this.norm = obj.\("norm").as[String]
    
    this.ec = res("ec")
    //this.ec = obj.\("ec").as[String]
  }
 
  
}
case class AEmailExt(var fileName: String,var model: AEmailModel,var department: String ="Unassigned",product: Array[String] = Array[String](), var emotions: String,var prime: String="Unknown",var norm: String="",var ec: String="") {
  //println("Emotions " + emotions)
  if (emotions != "none"){ 
    //val res = emotions.parseJson
    val obj = emotions.parseJson //Json.parse(emotions)
    val res = obj.convertTo[Map[String, String]]
    
    this.prime = res("prime")
    //this.prime = obj.\("prime").as[String]
    this.prime = this.prime.replaceAll("\"", "")
    
    var n = res("norm")
    //var n = obj.\("norm")
    
    
    this.norm = res("norm")
    //this.norm = obj.\("norm").as[String]
    
    this.ec = res("ec")
    //this.ec = obj.\("ec").as[String]
  }
}

object ModelJsonImplicits extends DefaultJsonProtocol {
  implicit val impSenderInfo = jsonFormat4(SenderInfo)
  implicit val impGender = jsonFormat2(Gender)
  implicit val impEmailHeader = jsonFormat2(EmailHeader)
  implicit val impHeaders = jsonFormat1(Headers)
  implicit val impLocation = jsonFormat2(Location)
  implicit val impGeoInfo = jsonFormat11(GeoInfo)
  implicit val impEmailModel = jsonFormat11(EmailModel)
  implicit val impSimpleEmailModel = jsonFormat13(SimpleEmailModel)
  implicit val impSimpleEmailExt = jsonFormat8(SimpleEmailExt)
}

/*
object Gender {
  implicit val genderWrites = Json.writes[Gender]
}
object GeoInfo {
  implicit val geoinfoWrites = Json.writes[GeoInfo]
  implicit val geoinfoReads = Json.reads[GeoInfo]
}
object Deception {
  implicit val deceptionWrites = Json.writes[Deception]
}
object Location {
  implicit val locationWrites = Json.writes[Location]
}
object EmailModel {

  implicit val addressWrites = new Writes[Address] {
    def writes(a: Address) = Json.obj(
      "Address" -> a.toString())
  }

  implicit val senderInfoWrites = Json.writes[SenderInfo]
  implicit val headerWrites = Json.writes[EmailHeader]

  implicit val senderInfoReads = Json.reads[SenderInfo]
  implicit val headerReads = Json.reads[EmailHeader]

  implicit val emailWrites = Json.writes[EmailModel]
  implicit val emailReads = Json.reads[EmailModel]

}
object EmailExt {
  implicit val emailExtWrites = Json.writes[EmailExt]
}

object SimpleEmailModel {
  implicit val headerWrites = new Writes[Headers] {
    def writes(a: Headers) = Json.obj(
      "Headers" -> "")
  }
  //implicit val headerReads = Json.reads[Headers]
  implicit val simpleEmailModel = Json.writes[SimpleEmailModel]
}
object SimpleEmailExt {
  implicit val simpleEmailExtWrites = Json.writes[SimpleEmailExt]
}
object AEmailModel {
  implicit val emailExtWrites = Json.writes[AEmailModel]
}
object AEmailExt {
  implicit val simpleEmailExtWrites = Json.writes[AEmailExt]
}
* */
