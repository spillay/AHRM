package com.dsleng.service

import org.elasticsearch.client.{Response, ResponseListener, RestClient}
import org.apache.http.HttpHost
import org.apache.http.util.EntityUtils
import org.apache.http.nio.entity.NStringEntity
import org.apache.http.entity.ContentType
import java.util.Collections
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json._

import org.apache.commons._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.HttpPost
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.HttpHeaders
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import play.api.Logger

case class queryStruct(query: String);
case class emoStruct(text: String);

object queryStruct {
  implicit val qsWrites = new Writes[queryStruct] {
    def writes(qs: queryStruct) = Json.obj(
      "query" -> qs.query
    )
  }
}
object emoStruct {
  implicit val emoWrites = new Writes[emoStruct] {
    def writes(qs: emoStruct) = Json.obj(
      "text" -> qs.text
    )
  }
}

class ESService(server: String) {
  println("Starting Simple App")
  
  def getSyncData(index: String,query: String): String ={
    println("in getSyncData")
    var restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http")
       ).build();
    println("after create")
    val params = Collections.singletonMap("pretty", "true");
    val qs = new queryStruct(query)
    val jsonString = Json.toJson(qs);//"{\"query\": {\"match_all\": {}}}";
    var js = Json.stringify(jsonString)
    js = js.replaceAll("\\\\\"","\"")
    println("js value",js)
    val entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
    val indexc = index.replaceAll("\"","")
    //val indexq = indexc + "/_search";
    val indexq = "all-items/_search";
    var response = restClient.performRequest("GET", indexq,params,entity)
    var responseBody = EntityUtils.toString(response.getEntity())
    restClient.close();
    return responseBody;
  }
  def getData(query: String,res: ResponseListener){
     var restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http")
       ).build();

    val params = Collections.singletonMap("pretty", "true");
    val jsonString = "{\"query\": {\"match_all\": {}}}";
    val entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
    restClient.performRequestAsync("GET", "all-items/_search",params,entity,res);
    restClient.close();
  }
   def getEmotion(msg: String): String = {
      val emo = new emoStruct(msg);
      val jsonString = Json.toJson(emo);
      val post = new HttpPost("http://localhost:5000/web/get")
      post.setEntity(new StringEntity(Json.stringify(jsonString)))
      
      post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      //post.addHeader("Access-Control-Allow-Origin","www.examplesite.com")
      // send the post request
      var client = HttpClientBuilder.create().build();
      //val client = new DefaultHttpClient
       try { 
          val response = client.execute(post)
          println(response);
          response.getAllHeaders.foreach(arg => println(arg))
          val res = response.getEntity();
          val content = EntityUtils.toString(res);
          println("++++++++++++++++++++++++++++++++++++",content);
          content;
      } catch {
        case ioe: IOException => {
          Logger("analytics").debug(s"ESService:getEmotion IOException ${ioe}")
          ("IOException" + ioe)
        }
        case e: Exception => {
          Logger("analytics").debug(s"ESService:getDeception Exception ${e}")
          ("Exception" + e)
        }
      }
     
  }
   def getEntropy(msg: String): String = {
      val emo = new emoStruct(msg);
      val jsonString = Json.toJson(emo);
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>.",jsonString);
      val post = new HttpPost("http://localhost:5000/emo/entropy")
      post.setEntity(new StringEntity(Json.stringify(jsonString)))
      
      post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      //post.addHeader("Access-Control-Allow-Origin","www.examplesite.com")
      // send the post request
      try {
        var client = HttpClientBuilder.create().build();
        //val client = new DefaultHttpClient
        val response = client.execute(post)
        println(response);
        response.getAllHeaders.foreach(arg => println(arg))
        val res = response.getEntity();
        println(res);
        val content = EntityUtils.toString(res);
      content;
      } catch {
        case ioe: IOException => {
          Logger("analytics").debug(s"ESService:getEntropy IOException ${ioe}")
          ("IOException" + ioe)
        }
        case e: Exception => {
          Logger("analytics").debug(s"ESService:getDeception Exception ${e}")
          ("Exception" + e)
        }
      }
  }
   def getDeception(msg: String): String = {
      val emo = new emoStruct(msg);
      val jsonString = Json.toJson(emo);
      val post = new HttpPost("http://localhost:5002/deception/get")
      post.setEntity(new StringEntity(Json.stringify(jsonString)))
      
      post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      //post.addHeader("Access-Control-Allow-Origin","www.examplesite.com")
      // send the post request
      var client = HttpClientBuilder.create().build();
      //val client = new DefaultHttpClient
      try {
        val response = client.execute(post)
        println(response);
        response.getAllHeaders.foreach(arg => println(arg))
        val res = response.getEntity();
        println(res);
        val content = EntityUtils.toString(res);
        content;
      } catch {
        case ioe: IOException => {
          Logger("analytics").debug(s"ESService:getDeception IOException ${ioe}")
          ("IOException" + ioe)
        }
        case e: Exception => {
          Logger("analytics").debug(s"ESService:getDeception Exception ${e}")
          ("Exception" + e)
        }
      }
  }
}