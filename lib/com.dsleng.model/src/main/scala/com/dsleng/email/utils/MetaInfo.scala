package com.dsleng.email.utils

import java.io.File
import scala.io.Source
import java.io.PrintWriter
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import java.net.InetAddress
import com.dsleng.email.DummySenderInfo
import com.dsleng.email.RecipientInfo
import com.dsleng.email.DummySenderInfoExt

import com.dsleng.email.GeoInfo

import spray.json._
import spray.json.DefaultJsonProtocol._

class MetaInfo(baseDir: String) {
  var deptMap = ArrayBuffer[RecipientInfo]()
  var inbound = ArrayBuffer[DummySenderInfo]()
  var inboundbatch = ArrayBuffer[DummySenderInfoExt]()
  val prod = new ProductMgr()
  val names = "randNames.txt"
  val depts = List("Finance","Operations","Information Technology","Human Resources","Executive")
  var senderCnt = 0
  val IPCheck = new IPTZ()
  private def randomIp: String = {
		val random = Random
		var octets = ArrayBuffer[Int]()
		octets += random.nextInt(223) + 1
		(1 to 3).foreach { _ => octets += random.nextInt(255) }
		octets.mkString(".")
	}
  def log(msg: String){
    //println(msg)
  }
  def getRandom(): Int ={
    val random = Random
    return random.nextInt(100)
  }
  def getSender(datetime: String): DummySenderInfo ={
    var sen = inbound(senderCnt)
    log("DateTime from getSender " + datetime)
    while( (IPCheck.checkTZ(sen.IP, datetime)) ){
      senderCnt = senderCnt + 1
      sen = inbound(senderCnt)
    }
    senderCnt = senderCnt + 1
    if (senderCnt == inbound.length ){
      senderCnt = 0
    }
    return sen
  }
  def getSenderBatch(datetime: String): DummySenderInfoExt ={
    var sen = inboundbatch(senderCnt)
    log("DateTime from getSender " + datetime)
    while( (IPCheck.checkTZ(sen, datetime)) ){
      val r = new RandomGen()
      // Jumping to avoid pattern
      if ( r.getRandomJump(0, 1) == 0 ){
        //longer jump
        senderCnt = r.getRandomJump(senderCnt, inboundbatch.length)
      } else {
        // short jump
        senderCnt = senderCnt + 1
      }
      if (senderCnt == inboundbatch.length ){
       senderCnt = 0
      }
      sen = inboundbatch(senderCnt)
    }
    senderCnt = senderCnt + 1
    if (senderCnt == inboundbatch.length ){
      senderCnt = 0
    }
    return sen
  }
  def getRecipient(): RecipientInfo ={
    return deptMap(getRandom)
  }
  def Load(){
    val dmap = baseDir + File.separator + "dept-mapping.txt"
    val in = baseDir + File.separator + "inbound-mapping.txt"
    
    Source.fromFile(dmap).getLines().foreach(s=>{
      val data = s.split(":")
      val r = new RecipientInfo(data(2),data(1))
      deptMap += r
    })
    Source.fromFile(in).getLines().foreach(s=>{
      val data = s.split(":")
      val r = new DummySenderInfo(data(0),data(1))
      inbound += r
      
      val sendergeoip = IPCheck.getInfo(r.IP)
      val res = sendergeoip.parseJson//Json.parse(sendergeoip)
      val parsed = res.convertTo[Map[String, String]]
      val geoinfo = new GeoInfo(
          parsed("ip"),
          parsed("country_code"),
          parsed("country_name"),
          parsed("region_code"),
          parsed("region_name"),
          parsed("city"),
          parsed("zip_code"),
          parsed("time_zone"),
          parsed("latitude"),
          parsed("longitude"),
          parsed("metro_code"))
      val tz = IPCheck.getTimeZone(r.IP)
      val parts = r.Email.split("@")
      println(parts(1))
      val pds = prod.getProducts(parts(1))
      pds.foreach(println)
      val sr = new DummySenderInfoExt(r.Email,r.IP,geoinfo,tz,pds)
      inboundbatch += sr
    })
  }
  def Process(){
    val dmap = new PrintWriter(new File( baseDir + File.separator + "dept-mapping.txt"))
    val in = new PrintWriter(new File( baseDir + File.separator + "inbound-mapping.txt"))
    
    val fileName = baseDir + File.separator + names
    var cnt = 0
    var idx = 0
    for (line <- Source.fromFile(fileName).getLines) {
      //log(line)
      val regex = """(\S+\s)(\S+)""".r
      val nme = regex.findFirstMatchIn(line)
      val regex(f,s) = line
      dmap.write(f.trim() + ":" + depts(idx) + ":" + f.trim() + "@example.com\n")
      //log(f.trim() + ":" + depts(idx))
      var ip = this.randomIp
      while(InetAddress.getByName(ip).isSiteLocalAddress()){
        ip = this.randomIp
      }
      in.write(s + "@external.com:" + ip + "\n")
      cnt = cnt +1 
      if (cnt == 20 ){
        idx = idx +1
        cnt = 0
      }
    }
    dmap.close()
    in.close()
    
  }
}

object MetaInfo {
  def main(args: Array[String]) {
      val IPCheck = new IPTZ()
      val sendergeoip = IPCheck.getInfo("1.2.3.4")
      val res = sendergeoip.parseJson//Json.parse(sendergeoip)
      val parsed = res.convertTo[Map[String, String]]
      val geoinfo = new GeoInfo(
          parsed("ip"),
          parsed("country_code"),
          parsed("country_name"),
          parsed("region_code"),
          parsed("region_name"),
          parsed("city"),
          parsed("zip_code"),
          parsed("time_zone"),
          parsed("latitude"),
          parsed("longitude"),
          parsed("metro_code"))
  }
}
