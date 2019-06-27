package com.dsleng.email.utils

import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import com.dsleng.email.Location
import com.dsleng.email.GeoInfo
import com.dsleng.email.DummySenderInfoExt

import spray.json._
import spray.json.DefaultJsonProtocol._

class IPTZ {
  val startHour = 8
  val endHour = 19
  val rand = new RandomGen()
  def log(msg: String){
    //println(msg)
  }
  def getIP(ip: String): String ={
    val res= get("http://localhost:4000/json/" + ip)
    return res
  }
  def checkTZ(sen: DummySenderInfoExt,datetime: String): Boolean={
    val dt = this.getTimeZoneBatch(sen.tz, datetime)
    if ( dt == null ){ return false }
    log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.Hour of Day" + dt.getHourOfDay)
    if (!(dt.getHourOfDay() > startHour && dt.getHourOfDay() <= endHour)){
      return false
      val rd = rand.getRandomTZ()
      log(">>>>>>>>>>>>>RandomTZ: " + rd)
      if (rd == 1){
        return true
      } else {
        return false
      }
    } else {
      return true
    }
  }
  def checkTZ(ip: String,datetime: String): Boolean={
    val dt = this.getTimeZone(ip, datetime)
    if ( dt == null ){ return false }
    if (!(dt.getHourOfDay() > startHour && dt.getHourOfDay() <= endHour)){
      val rd = rand.getRandomTZ()
      log(rd.toString())
      if (rd == 1){
        return true
      } else {
        return false
      }
    } else {
      return true
    }
  }
  def getInfo(ip: String): String={
     val res= get("http://localhost:4000/json/" + ip)
     val parsed = res.parseJson
     return parsed.toString()
  }
  def getLocation(ip: String): Location={
    val res= get("http://localhost:4000/json/" + ip)
    val obj = res.parseJson
    val parsed = obj.convertTo[Map[String, String]]
    val lat = parsed("latitude")
    val lon = parsed("longitude")
    log("lat " + lat)
    return new Location(lat.toString(),lon.toString())
  }
  def getGeoInfo(ip: String): GeoInfo ={
      val sendergeoip= get("http://localhost:4000/json/" + ip)
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
      return geoinfo
  }
  def getTimeZone(ip: String): String={
    val res= get("http://localhost:4000/json/" + ip)
    val obj = res.parseJson
    val parsed = obj.convertTo[Map[String, String]]
    val tz = parsed("time_zone")
    if ( tz == "" ){ return null }
    else { return tz }
  }
  def getTimeZone(ip: String,datetime: String): DateTime={
    val res= get("http://localhost:4000/json/" + ip)
    val obj = res.parseJson
    val parsed = obj.convertTo[Map[String, String]]
    val tz = parsed("time_zone")
    if ( tz == "" ){ return null }
    log("Time Zone: " + tz + " for ip " + ip)
    log("DateTime " + datetime)
    val dtz = DateTimeZone.forID(tz)
    val dtf = DateTimeFormat.forPattern("MM-dd-yyyy'T'HH:mm:ss");
    val jodatime = dtf.parseDateTime(datetime);
    log(jodatime.toString())
    val tztime =  jodatime.toDateTime(dtz)
    log("Timezone " + jodatime.toDateTime(dtz))
    return tztime
  }
  def getTimeZoneBatch(tz: String,datetime: String): DateTime={
    val dtz = DateTimeZone.forID(tz)
    val dtf = DateTimeFormat.forPattern("MM-dd-yyyy'T'HH:mm:ss");
    val jodatime = dtf.parseDateTime(datetime);
    log(jodatime.toString())
    val tztime =  jodatime.toDateTime(dtz)
    log("Timezone " + jodatime.toDateTime(dtz) + " tz " + tz)
    return tztime
  }
  def get(url: String) = scala.io.Source.fromURL(url).mkString
}