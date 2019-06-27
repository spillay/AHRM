package com.dsleng.emo.helper

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame
import play.api.libs.json._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SPFunctions.addOneCustomNative
import org.apache.spark.sql.SPFunctions.checkforFeature
import org.apache.spark.sql.SPFunctions.checkforFeature2


import org.apache.spark.sql.catalyst.expressions.SPArrayContains
import org.apache.spark.sql.catalyst.expressions.SPArrayContains2
import org.apache.spark.sql.catalyst.expressions.Literal
import org.apache.spark.sql.catalyst.expressions.ArrayContains
import org.apache.spark.sql.catalyst.expressions.Size
import org.apache.spark.sql.types._
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenContext
import org.apache.spark.sql.execution.command.ExplainCommand
import org.apache.spark.sql.execution.debug._
import org.apache.spark.sql.functions.typedLit
import org.apache.spark.sql.functions.array_contains
import org.apache.spark.serializer.JavaSerializer
import org.apache.spark.sql.catalyst.analysis.{ResolveTimeZone, SimpleAnalyzer}
import org.apache.spark.sql.internal.SQLConf
import scala.util.control.Breaks._

class Emotions(spark: SparkSession) {
  import spark.sqlContext.implicits._
  
  val complete = "/Data/emo-store/dict-data/emo-df.parquet"
  val complete2 = "/Data/emo-store/dict-data/emo-dfv2.parquet"
  val emoOrder = Array("Shame", "Fear", "Anger", "Disgust", "Sadness", "Anxiety", "Relief", "Pride", "Interest", "Agreeableness", "Contentment", "Joy")

  var odf = spark.read.parquet(complete)
  
  def execute(tokens: Seq[String]): String = {
     val ndf = process(tokens)
     return convertToJson(ndf)
   }
   def process(tokens: Seq[String]): DataFrame = {
    var df = odf
      tokens.foreach(s=>{
        df = df.withColumn("liwc_fwords", checkforFeature(col("liwc_words"),lit(s),col("liwc_fwords")))
        df = df.withColumn("ext_fwords", checkforFeature(col("ext_words"),lit(s),col("ext_fwords")))
      })  
      df = df.withColumn("liwc_count", size(col("liwc_fwords")))
      df = df.withColumn("ext_count", size(col("ext_fwords")))
      df = df.select("emotion","liwc_fwords", "liwc_count","ext_fwords", "ext_count")
      df = df.withColumn("liwc_set", array_distinct(col("liwc_fwords")))
      df = df.withColumn("ext_set", array_distinct(col("ext_fwords")))
      df = df.withColumn("union",array_union(col("liwc_set"),col("ext_set")))
      df = df.withColumn("count",size(col("union")))
      df = df.filter(col("count")>0) 
      return df.select("emotion","union", "count")
   }
   def convertToJson(df: DataFrame): String = {
      var res = Seq[EmoData]()
      df.collect().foreach(r => {
        val e = new EmoData(
            r.getAs[String]("emotion"),
            r.getAs[Integer]("count"),
            r.getAs[Seq[String]]("union"))
        res = res :+ e
      })    
      val prime = this.getPrime(res)   
      val out = new EmoRes(res,prime)
      val js = Json.toJson(out)
      println(js.toString())
      return js.toString() 
   }
   def getPrime(emo: Seq[EmoData]): Seq[EmoData] = {
    if (emo.isEmpty) return Seq[EmoData](new EmoData("Unknown",0,Seq[String]()))
    val max = emo.maxBy(f => f.cnt)
    val res = emo.filter(p => p.cnt == max.cnt)
    var prime: Seq[EmoData] = null
    if (res.length > 1) {
      breakable {
        for (i <- emoOrder.indices) {
          val fd = res.filter(e => e.emotion.compareToIgnoreCase(emoOrder(i)) == 0)
          if (fd.length > 0) {
            prime = fd
            break
          }
        }
      }
    } else {
      prime = res
    }
    return prime
  }
}