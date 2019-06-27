package com.dsleng.server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.http.scaladsl.model.{ResponseEntity,HttpResponse}
import akka.annotation.ApiMayChange
import akka.http.scaladsl.model.StatusCodes

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame
//import com.dsleng.emo.helper.Emotions
//import play.api.libs.json._

@ApiMayChange
object WebServer extends HttpApp {
  def appName: String = "Analysis Services"
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
  //val emo = new Emotions(spark)
  override def routes: Route =
    path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
    } ~
    path("test") {
      parameter('id.as[Int]) { id =>
        post { 
          complete {
            s"CustId: ${id}"
          }
        }
      }
    } ~
    path("emo") { 
      post {
        entity(as[String]){ param =>{
          println(param)
//          val js = Json.parse(param)
//          val tokens = (js \ "tokens" ).get
//          val stoks = tokens.as[List[String]]
          val stoks = List("terrible","funny","story","peacefully","foolproof","amaze","forgive")
          val res = "testing"//emo.execute(stoks)
          val resp: ResponseEntity = HttpEntity(ContentTypes.`application/json`,res)
          complete(HttpResponse(StatusCodes.OK, entity = resp))
        }
       }
      }
    }
  
}

