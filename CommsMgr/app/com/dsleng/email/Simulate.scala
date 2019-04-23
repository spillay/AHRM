package com.dsleng.email

import scala.collection.mutable.ArrayBuffer
import com.dsleng.email.utils.RandomGen
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes,JsArray }
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class Person(email: String,emptype: String,dept: String)
case class Emotion(name: String,dir: Boolean)
case class EmoUnit(name: String,var count: Double)
object EmoUnit {
  implicit val emoUnitWrites = new Writes[EmoUnit] {
    def writes(a: EmoUnit) = Json.obj(
      a.name -> a.count.toString()
    )
  }
}
case class EmoStr(ec: ArrayBuffer[EmoUnit]){
  def getEC(): String ={
    val ar = JsArray(ec.map(e=>Json.toJson(e)))
    ar.toString()
  }
  def getNorm(): String = {
    val total = ec.reduce((a,b)=>new EmoUnit("total",a.count+b.count))
    println(total.count)
    val norm = ec.map(e=>new EmoUnit(e.name,e.count/total.count))
    var res = "{"
    var i = 0
    norm.foreach(e=>{
      res += "\"" + e.name + "\":\"" + e.count + "\"" 
      println(i,norm.length)
      if (i!=norm.length-1){
        res += ","
      }
      i=i+1
    })
    res += "}"
    
    //val ar = JsArray(norm.map(e=>Json.toJson(e)))
    //ar.toString()
    return res
  }
  def getPrime(): String={
    val res = ec.sortBy(-_.count)
    
    println(res)
    return "\"" + res(0).name + "\""
  }
}



object EmoStr {
  implicit val emoStrWrites = new Writes[EmoStr] {
    def writes(a: EmoStr) = Json.obj(
      "ec" -> a.getEC(),
      "norm" -> a.getNorm(),
      "prime" -> a.getPrime()
    )
  }
}

class Emos {
  var ran = new RandomGen()
  var emos = new ArrayBuffer[Emotion]()
  emos += new Emotion("Fear",false)
  emos += new Emotion("Sadness",false)
  emos += new Emotion("Disgust",false)
  emos += new Emotion("Anxiety",false)
 
  emos += new Emotion("Joy",true)
  emos += new Emotion("Contentment",true)
  emos += new Emotion("Pride",true)
  emos += new Emotion("Agreeableness",true)
  emos += new Emotion("Relief",true)
  emos += new Emotion("Interest",true)
  
  val pe = emos.filter(e=>e.dir==true)
  val ne = emos.filter(e=>e.dir==false)
  
  def getPos(): String = {
    val r = ran.getRandomJump(1, pe.length)
    println(r,pe)
    return pe(r-1).name
  }
   def getNeg(): String = {
    val r = ran.getRandomJump(1, ne.length)
    return ne(r-1).name
  }
   def getPosStr(): String = {
      var em = new ArrayBuffer[EmoUnit]()
      val r = ran.getRandomJump(1, 4)
      for(i<- 0 to r){
        val c = ran.getRandomJump(1, 6)
        em += new EmoUnit(getPos(),c)
      }
      var cem = new ArrayBuffer[EmoUnit]()
      em.foreach(p=>{
        em.foreach(c=>{
          if (p.name == c.name){
            p.count = p.count + c.count
            //c.count = 0
          }
        })
        cem += new EmoUnit(p.name,p.count)
      })
      val oj = new EmoStr(cem)
      val j = Json.toJson(oj)
      (j.toString())
   }
    def getNegStr(): String = {
      var em = new ArrayBuffer[EmoUnit]()
      val r = ran.getRandomJump(1, 4)
      for(i<- 0 to r){
        val c = ran.getRandomJump(1, 6)
        em += new EmoUnit(getNeg(),c)
      }
      var cem = new ArrayBuffer[EmoUnit]()
      em.foreach(p=>{
        em.foreach(c=>{
          if (p.name == c.name){
            p.count = p.count + c.count
            //c.count = 0
          }
        })
        cem += new EmoUnit(p.name,p.count)
      })
      val oj = new EmoStr(cem)
      val j = Json.toJson(oj)
      (j.toString())
   }
}




class Simulate {
  var emails = new ArrayBuffer[AEmailExt]()
  
  def getAll(): ArrayBuffer[AEmailExt] = {
    this.processDept()
    return emails
  }
  def getEmail(from: Person,to: Person,dte: String,emo: String): AEmailExt ={
     var simpleModel = AEmailModel(
      from.email,
      to.email,
      dte,
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
        emo
    )
     return sEmail
  }
  def processDept(){
    var pl = new ArrayBuffer[Person]()
    pl += new Person("john@example.com","manager","operations")
    pl += new Person("peter@example.com","employee","operations")
    pl += new Person("paul@example.com","employee","operations")
    pl += new Person("mark@example.com","employee","operations")
    pl += new Person("mathew@example.com","employee","operations")
    
    println(pl.size)
    var ran = new RandomGen()
    val form = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
    var dte = form.parseDateTime("2018-04-12T12:00:00")
    
    
    val mgr = pl.filter(p=>{p.emptype=="manager"})
    val emp = pl.filter(p=>{p.emptype=="employee"})
    val eHandle = new Emos()
    var a = 0
    // 168 gives a week
    // 168x4 = 672
    // 8064 is a year
    for(a <- 0 to 8064){
      val pn = ran.getRandom(0, 1)
      mgr.foreach(m=>{
        emp.foreach(e=>{
          if (pn == 0){
            emails += getEmail(m, e, dte.toString(form),eHandle.getPosStr())
          } else {
            emails += getEmail(m, e, dte.toString(form),eHandle.getNegStr())
          }
        })
      })
      emp.foreach(e=>{
          val i = ran.getRandom(1, emp.length-1)
          if (e.email != emp(i).email){
            if (pn == 0){
              emails += getEmail(e, emp(i), dte.toString(form),eHandle.getPosStr())
            }else{
              emails += getEmail(e, emp(i), dte.toString(form),eHandle.getNegStr())
            }
          }
      })
      dte = dte.plusHours(1)
    }
  
  }
}
object Simulate extends App {
  Console.println("Hello World: " + (args mkString ", "))
  val emos = new ArrayBuffer[EmoUnit]()
  emos += new EmoUnit("Fear",2)
  emos += new EmoUnit("Anger",3)
  emos += new EmoUnit("Disgust",5)
  val oj = new EmoStr(emos)
  oj.getNorm()
  val j = Json.toJson(oj)
  println(j.toString())
}