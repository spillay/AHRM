package com.dsleng.emo.helper

import org.apache.spark.sql.SparkSession
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.sql.Date
import org.apache.spark.sql.types.{DateType, IntegerType}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.sum

case class emoInfo(Agreeableness: Int, Anger: Int,dt: Date)
object emoInfo {
  implicit val emoInfoReads: Reads[emoInfo] = (
    (JsPath \ "Agreeableness").read[Int] and
    (JsPath \ "Anger").read[Int] and
    (JsPath \ "date").read[Date])(emoInfo.apply _)
}

class EntropyHelper(val spark: SparkSession) {
  import spark.sqlContext.implicits._
  
  def process(data: JsValue): String = {
    println("In EntropyHelper.process")
    val ndata = Json.parse(this.getData())
    var emotions  = List[emoInfo]()
    ndata match {
      case JsArray(da) => {
        //val res = spark.sparkContext.parallelize(da.toSeq)
        da.foreach(f => {
          f.validate[emoInfo] match {
            case s: JsSuccess[emoInfo] => {
              val emo: emoInfo = s.get
              emotions = emotions.::(emo)
            }
            case e: JsError => {
              // error handling flow
            }
          }
        })
      }
      case _ =>
    }
    println(emotions)
    val df = emotions.toDF()
    df.show()
    df.printSchema()
    df.groupBy("dt").agg(sum("Agreeableness") as "Total_Agreeableness",sum("Anger") as "Total_Anger").show()

    return data.toString()
  }
  def getData(): String = {
    var jsonArray = new JsArray
    val data1 = Json.obj("Agreeableness" -> 1, "Anger" -> 0, "Anxiety" -> 1, "Contentment" -> 0, "Disgust" -> 0, "Fear" -> 0, "Interest" -> 1, "Joy" -> 0, "Pride" -> 0, "Relief" -> 0, "Sadness" -> 0, "Shame" -> 0, "Word-Agreeableness" -> 0, "Word-Anger" -> 0, "Word-Anxiety" -> 0, "Word-Contentment" -> 0, "Word-Disgust" -> 0, "Word-Fear" -> 0, "Word-Word-Interest" -> 0, "Word-Joy" -> 0, "Word-Pride" -> 0, "Word-Relief" -> 0, "Word-Sadness" -> 0, "Word-Shame" -> 0, "date" -> "2001-03-15", "time" -> "2001-03-15 16:01:00", "neg" -> 0, "sender" -> 0, "s..shively@enron.com" -> 0)
    jsonArray = jsonArray.+:(data1)
    val data11 = Json.obj("Agreeableness" -> 1, "Anger" -> 0, "Anxiety" -> 1, "Contentment" -> 0, "Disgust" -> 0, "Fear" -> 0, "Interest" -> 1, "Joy" -> 0, "Pride" -> 0, "Relief" -> 0, "Sadness" -> 0, "Shame" -> 0, "Word-Agreeableness" -> 0, "Word-Anger" -> 0, "Word-Anxiety" -> 0, "Word-Contentment" -> 0, "Word-Disgust" -> 0, "Word-Fear" -> 0, "Word-Word-Interest" -> 0, "Word-Joy" -> 0, "Word-Pride" -> 0, "Word-Relief" -> 0, "Word-Sadness" -> 0, "Word-Shame" -> 0, "date" -> "2001-03-15", "time" -> "2001-03-15 16:01:00", "neg" -> 0, "sender" -> 0, "s..shively@enron.com" -> 0)
    jsonArray = jsonArray.+:(data11)
    val data2 = Json.obj("Agreeableness" -> 0, "Anger" -> 1, "Anxiety" -> 0, "Contentment" -> 0, "Disgust" -> 0, "Fear" -> 0, "Interest" -> 1, "Joy" -> 0, "Pride" -> 0, "Relief" -> 0, "Sadness" -> 0, "Shame" -> 0, "Word-Agreeableness" -> 0, "Word-Anger" -> 0, "Word-Anxiety" -> 0, "Word-Contentment" -> 0, "Word-Disgust" -> 0, "Word-Fear" -> 0, "Word-Word-Interest" -> 0, "Word-Joy" -> 0, "Word-Pride" -> 0, "Word-Relief" -> 0, "Word-Sadness" -> 0, "Word-Shame" -> 0, "date" -> "2001-03-16", "time" -> "2001-03-16 16:01:00", "neg" -> 0, "sender" -> 0, "s..shively@enron.com" -> 0)
    jsonArray = jsonArray.+:(data2)
    val data3 = Json.obj("Agreeableness" -> 0, "Anger" -> 0, "Anxiety" -> 0, "Contentment" -> 0, "Disgust" -> 1, "Fear" -> 1, "Interest" -> 1, "Joy" -> 0, "Pride" -> 0, "Relief" -> 0, "Sadness" -> 0, "Shame" -> 0, "Word-Agreeableness" -> 0, "Word-Anger" -> 0, "Word-Anxiety" -> 0, "Word-Contentment" -> 0, "Word-Disgust" -> 0, "Word-Fear" -> 0, "Word-Word-Interest" -> 0, "Word-Joy" -> 0, "Word-Pride" -> 0, "Word-Relief" -> 0, "Word-Sadness" -> 0, "Word-Shame" -> 0, "date" -> "2001-03-17", "time" -> "2001-03-17 16:01:00", "neg" -> 0, "sender" -> 0, "s..shively@enron.com" -> 0)
    jsonArray = jsonArray.+:(data3)
    val data4 = Json.obj("Agreeableness" -> 0, "Anger" -> 0, "Anxiety" -> 1, "Contentment" -> 0, "Disgust" -> 0, "Fear" -> 0, "Interest" -> 1, "Joy" -> 0, "Pride" -> 1, "Relief" -> 1, "Sadness" -> 0, "Shame" -> 0, "Word-Agreeableness" -> 0, "Word-Anger" -> 0, "Word-Anxiety" -> 0, "Word-Contentment" -> 0, "Word-Disgust" -> 0, "Word-Fear" -> 0, "Word-Word-Interest" -> 0, "Word-Joy" -> 0, "Word-Pride" -> 0, "Word-Relief" -> 0, "Word-Sadness" -> 0, "Word-Shame" -> 0, "date" -> "2001-03-18", "time" -> "2001-03-18 16:01:00", "neg" -> 0, "sender" -> 0, "s..shively@enron.com" -> 0)
    jsonArray = jsonArray.+:(data4)
    val data5 = Json.obj("Agreeableness" -> 0, "Anger" -> 0, "Anxiety" -> 1, "Contentment" -> 0, "Disgust" -> 0, "Fear" -> 0, "Interest" -> 1, "Joy" -> 1, "Pride" -> 0, "Relief" -> 0, "Sadness" -> 0, "Shame" -> 0, "Word-Agreeableness" -> 0, "Word-Anger" -> 0, "Word-Anxiety" -> 0, "Word-Contentment" -> 0, "Word-Disgust" -> 0, "Word-Fear" -> 0, "Word-Word-Interest" -> 0, "Word-Joy" -> 0, "Word-Pride" -> 0, "Word-Relief" -> 0, "Word-Sadness" -> 0, "Word-Shame" -> 0, "date" -> "2001-03-19", "time" -> "2001-03-19 16:01:00", "neg" -> 0, "sender" -> 0, "s..shively@enron.com" -> 0)
    jsonArray = jsonArray.+:(data5)
    println(jsonArray.toString())
    return jsonArray.toString()
  }

}