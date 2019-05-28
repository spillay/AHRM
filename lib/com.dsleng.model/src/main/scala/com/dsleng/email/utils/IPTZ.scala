package com.dsleng.email.utils
import play.api.libs.json._
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsValue.jsValueToJsLookup
import com.dsleng.email.Location
import com.dsleng.email.GeoInfo
import com.dsleng.email.DummySenderInfoExt

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
     val parsed = Json.parse(res)
     return parsed.toString()
  }
  def getLocation(ip: String): Location={
    val res= get("http://localhost:4000/json/" + ip)
    val parsed = Json.parse(res)
    val lat = (parsed \ "latitude").as[JsNumber].value
    val lon = (parsed \ "longitude").as[JsNumber].value
    log("lat " + lat)
    return new Location(lat.toString(),lon.toString())
  }
  def getGeoInfo(ip: String): GeoInfo ={
      val res= get("http://localhost:4000/json/" + ip)
      val parsed = Json.parse(res)
      (parsed \ "ip").as[JsString]
      val geoinfo = new GeoInfo(
          (parsed \ "ip").as[JsString].toString(),
          (parsed \ "country_code").as[JsString].toString(),
          (parsed \ "country_name").as[JsString].toString(),
          (parsed \ "region_code").as[JsString].toString(),
          (parsed \ "region_name").as[JsString].toString(),
          (parsed \ "city").as[JsString].toString(),
          (parsed \ "zip_code").as[JsString].toString(),
          (parsed \ "time_zone").as[JsString].toString(),
          (parsed \ "latitude").as[JsNumber].toString(),
          (parsed \ "longitude").as[JsNumber].toString(),
          (parsed \ "metro_code").as[JsNumber].toString())
      return geoinfo
  }
  def getTimeZone(ip: String): String={
    val res= get("http://localhost:4000/json/" + ip)
    val parsed = Json.parse(res)
    val tz = (parsed \ "time_zone").as[JsString].value
    if ( tz == "" ){ return null }
    else { return tz }
  }
  def getTimeZone(ip: String,datetime: String): DateTime={
    val res= get("http://localhost:4000/json/" + ip)
    val parsed = Json.parse(res)
    val tz = (parsed \ "time_zone").as[JsString].value
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