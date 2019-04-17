package controllers

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.mvc._
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.libs.json._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.dsleng.email.Gender
import com.dsleng.email.Deception
import com.dsleng.email._
import com.dsleng.email.utils.RandomGen

import scala.concurrent.ExecutionContext
import java.io.PrintWriter

class WebController @Inject() (ws: WSClient,cc: ControllerComponents) extends AbstractController(cc) {
  import ExecutionContext.Implicits.global
  val r = new RandomGen()
  def checkGender(msg: String): Future[String] ={
    val futureResponse = 
     ws.url("http://app.jaasuz.com:8000/init/gender_service/call/run/analyze_gender")
    .addQueryStringParameters("newtext" -> msg)
    .get()
    futureResponse.map{
      case resp: WSResponse => resp.body[String]
      case _ => "Failed"
    }
  }
  def checkDeception(msg: String): Future[String] ={
    val futureResponse = 
     ws.url("http://app.jaasuz.com:8000/init/deception_service/call/run/analyze")
    .addQueryStringParameters("newtext" -> msg)
    .get()
    futureResponse.map{
      case resp: WSResponse => resp.body[String]
      case _ => "Failed"
    }
  }
  def processEmail(model: EmailModel): JsValue ={
    val text = model.textContent
    for {
      gender <- checkGender(text)
      deception <- checkDeception(text)
    } yield {
      val ngender = gender.replace("(", "").replace(")", "").replaceAll("'", "")
      val parts = ngender.split(",")
      val g = new Gender(parts(0),parts(1))
      
      val dec = deception.replace("(", "").replace(")", "").replaceAll("'", "")
      val dparts = dec.split(",")
      val d = new Deception(parts(0),parts(1))
      
      val extModel = new EmailExt(model,g,d,"")
      return Json.toJson(extModel)
      //Ok(Json.obj("genderInfo" -> gender.mkString,"deceptionInfo" -> deception.mkString))
    }
    return Json.obj("Status" -> "Failed")
  }
  
  def RandomPrecentage(): Float ={
    val random = new scala.util.Random
    val res =  random.nextFloat()
    return res*100
  }
  def getGender(): Future[String] = Future {
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
    res;
  }
  def getDeception(): Future[String]=Future {
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
  def writeLogStash(msg: JsValue): Future[String] ={
    val futureResponse = 
     ws.url("http://localhost:5002/email/")
    .addHttpHeaders("Content-Type" -> "application/json")
    .post(msg)
    futureResponse.map{
      case resp: WSResponse => resp.body[String]
      case _ => "Failed"
    }
  }
  def analyze() = Action.async { implicit request =>
    //println(request.contentType.toString())
    //println(request.body)
    val emodel = request.body.asJson.get
    //println(emodel)
    val emailModel = emodel.as[EmailModel]
    //val text = "The dog ate all the food"
    val text = emailModel.textContent
    for {
      gender <- getGender()//checkGender(text)
      deception <- getDeception()//checkDeception(text)
    } yield {
      println(gender)
      println(deception)
      val ngender = gender.replace("(", "").replace(")", "").replaceAll("'", "").replaceAll("%", "")
      val gparts = ngender.split(",")
      var gen = ""
      if (gparts(0) == "male"){ gen = "Male" }
      if (gparts(0) == "female"){ gen = "Female" }
      val g = new Gender(gen,gparts(1))
      
      val dec = deception.replace("(", "").replace(")", "").replaceAll("'", "")
      val dparts = dec.split(",")
      val d = new Deception(dparts(0),dparts(1))
      val extModel = new EmailExt(emailModel,g,d,"")
      println(Json.toJson(extModel).toString())
      writeLogStash(Json.toJson(extModel)).map{
        case s: String => Ok(s)
      }
      //val pw = new PrintWriter("/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/upload.txt")
      //pw.write(Json.toJson(extModel).toString())
      //pw.close()
      Ok("Success")
      //Ok(Json.toJson(extModel))
      //Ok(Json.obj("genderInfo" -> gender.mkString,"deceptionInfo" -> deception.mkString))
    }
  }
}