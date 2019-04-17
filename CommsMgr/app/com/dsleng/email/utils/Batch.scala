package com.dsleng.email.utils

import java.io.File
import com.dsleng.email.EmailModel
import com.dsleng.email.Gender
import com.dsleng.email.Deception
import com.dsleng.email.EmailExt
import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class Batch(batchdir: String) {
  
  val r = new RandomGen()
  val batchfile = "batchfile"
  var indexcnt = 0
  var batchcnt = 1
  var counter = 0
  val batchsize = 60000
  
  def log(msg:String){
    // println(msg)
  }
  val dir = new File(batchdir)
  if (!dir.exists()){
    dir.mkdir()
  }
  var bfile = new java.io.PrintWriter(dir.getPath+File.separator+batchfile+batchcnt+".json")
  def close(){
    bfile.close()
  }
  def RandomPrecentage(): Float ={
    val random = new scala.util.Random
    val res =  random.nextFloat()
    return res*100
  }
  def getGender(): String ={
    //('male', '59.10%')
    val g = r.getRandomGen()
    var res = "('"
    g match {
      case 0 => res += "'male'"
      case 1 => res += "'female'"
    }
    res += ",'"
    res += this.RandomPrecentage()
    res +="%')"
    return res;
  }
  def getDeception(): String= {
    val g = r.getRandomDeception()
    var res = "('"
    g match {
      case 0 => res += "'Truth'"
      case 1 => res += "'Deceptive'"
    }
    res += ",'"
    res += this.RandomPrecentage()
    res +="')"
    res;
  }
  def process(email: String){
    log("Batch Process")
    val emodel: JsValue = Json.parse(email)
    val emailModel = emodel.as[EmailModel]
    process(emailModel)
  }
  def process(eModel: EmailModel){
     
      val text = eModel.textContent
   
      val gender = getGender()//checkGender(text)
      val deception = getDeception()//checkDeception(text)
    
      log(gender)
      log(deception)
      val ngender = gender.replace("(", "").replace(")", "").replaceAll("'", "").replaceAll("%", "")
      val gparts = ngender.split(",")
      var gen = ""
      if (gparts(0) == "male"){ gen = "Male" }
      if (gparts(0) == "female"){ gen = "Female" }
      val g = new Gender(gen,gparts(1))
        
      val dec = deception.replace("(", "").replace(")", "").replaceAll("'", "")
      val dparts = dec.split(",")
      val d = new Deception(dparts(0),dparts(1))
      val extModel = new EmailExt(eModel,g,d,"")
      bfile.write("{\"index\": {\"_index\":\"logstash-emails\",\"_type\":\"logstash-emails\"}}"+"\n")
      bfile.write(Json.toJson(extModel).toString()+"\n")
      indexcnt = indexcnt+1
      counter = counter+1
      if ( counter == batchsize ){
        bfile.close()
        batchcnt = batchcnt + 1
        bfile = new java.io.PrintWriter(dir.getPath+File.separator+batchfile+batchcnt+".json")
        counter = 0
      }
  }
}