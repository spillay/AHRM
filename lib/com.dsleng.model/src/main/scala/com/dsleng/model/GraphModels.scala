package com.analog42.model

import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes,JsArray }
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer

case class MinMax(place: Double,min: Double,max: Double)
case class MinMaxQues(place: Double,min: Double,max: Double,ques: Double,colProb: ListBuffer[PosProb]=null)
case class Ques(place: Double,value: Double)
case class Question(id: Int,q: String,prob: Double)
case class PosProb(name: String,place: Double,prob: Double)

case class ChartMinMax(ykey: String){
  var data: ListBuffer[MinMax] = new ListBuffer[MinMax]()
  var ques: ListBuffer[Ques] = new ListBuffer[Ques]()
  var posprobs = new ListBuffer[PosProb]()
  var qprob = 0.0
  init()
  
  def init(){
//    for(i <- 0 to 37){
//      posprobs += new ListBuffer[PosProb]()
//    }
  }
  
  // Merge MinMax and Ques and PosProb
  def getData(): ListBuffer[MinMaxQues]={
    println(data.mkString(","))
    println(ques.mkString(","))
    val nl = new ListBuffer[MinMaxQues]()
    //nl.+=(new MinMaxQues(0.0,0.0,0.0,0.0))
    // Change i value to ) to get data from place 0 which would be before the start of the sentence
    for(i <- 1 to data.length-1){
      val d = data.filter(e=>e.place==i.toDouble) 
      val q = ques.filter(e=>e.place==i.toDouble)
      println("data value ", d.mkString(","))
      if ( d.length > 0 && q.length > 0){
        val pospr = posprobs.filter(p=>p.place==i.toDouble)
        val n = new MinMaxQues(d(0).place,d(0).min,d(0).max,q(0).value,pospr)
        nl.+=(n)
      }
    }
    return nl
  }
}

case class ChartQuestion(ykey: String){
  var data: ListBuffer[Question] = new ListBuffer[Question]
}

object PosProb {
  //implicit val emWrites = Json.writes[EmailModel]
  implicit val ppReads = Json.reads[PosProb]
  implicit val ppWrites = new Writes[PosProb] {
    def writes(a: PosProb) = Json.obj(
      "pos" -> a.name,
      "prob" -> a.prob
  )}
}


object Question {
  //implicit val emWrites = Json.writes[EmailModel]
  implicit val qReads = Json.reads[Question]
  implicit val qWrites = new Writes[Question] {
    def writes(a: Question) = Json.obj(
      "place" -> a.id.toString(),
      //"name" -> a.q,
      "prob" -> a.prob
  )}
}
object ChartQuestion {
  implicit val qqWrites = new Writes[ChartQuestion] { 
    def writes(a: ChartQuestion) = Json.obj(
      "ykey" -> a.ykey,
      "data" -> JsArray(a.data.map(m=>Json.toJson(m)))
  )}
  def toJsonString(c: ChartQuestion): String={
    return Json.stringify(Json.toJson(c))
  }
}
object MinMaxQues {
  //implicit val emWrites = Json.writes[EmailModel]
  implicit val emReads = Json.reads[MinMaxQues]
  implicit val emWrites = new Writes[MinMaxQues] {
    def writes(a: MinMaxQues) = Json.obj(
      "place" -> a.place,
      "min" -> a.min,
      "max" -> a.max,
      "q_prob" -> a.ques,
      "matrix" -> JsArray(a.colProb.map(m=>Json.toJson(m)))
  )}
}

object MinMax {
  //implicit val emWrites = Json.writes[EmailModel]
  implicit val emReads = Json.reads[MinMax]
  implicit val emWrites = new Writes[MinMax] {
    def writes(a: MinMax) = Json.obj(
      "place" -> a.place,
      "min" -> a.min,
      "max" -> a.max
  )}
}
object ChartMinMax {
  //implicit val mmWrites = Json.writes[ChartMinMax]
  //implicit val mmReads = Json.reads[ChartMinMax]
  implicit val mmWrites = new Writes[ChartMinMax] { 
    def writes(a: ChartMinMax) = Json.obj(
      "type" -> "norm_prob",
	    "weight_id" -> "abcdef999",
	    "q_prob" -> a.qprob,
      if (a.ques.length == 0){
        "data" -> JsArray(a.data.map(m=>Json.toJson(m)))
      } else {
        "data" -> JsArray(a.getData().map(m=>Json.toJson(m)))
      }
  )}
  def toJsonString(c: ChartMinMax): String={
    return Json.stringify(Json.toJson(c))
  }
}