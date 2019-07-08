package com.dsleng.emo

import com.dsleng.model._
import com.dsleng.emo.helper._
import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SPFunctions.addOneCustomNative
import org.apache.spark.sql.SPFunctions.checkforFeature
import org.apache.spark.sql.SPFunctions.checkforFeature2

//import com.dsleng.tut.QuasiTest
import org.apache.spark.sql.catalyst.expressions.SPArrayContains
import org.apache.spark.sql.catalyst.expressions.SPArrayContains2
import org.apache.spark.sql.catalyst.expressions.Literal
import org.apache.spark.sql.catalyst.expressions.ArrayContains
import org.apache.spark.sql.catalyst.expressions.ArrayIntersect
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

import spray.json._
import spray.json.DefaultJsonProtocol._

class EmoSimple extends SparkHelper with Performance {
  import spark.sqlContext.implicits._
  

  val complete = "/Data/emo-store/dict-data/emo-df.parquet"
  val complete2 = "/Data/emo-store/dict-data/emo-dfv2.parquet"
  val emoOrder = Array("Shame", "Fear", "Anger", "Disgust", "Sadness", "Anxiety", "Relief", "Pride", "Interest", "Agreeableness", "Contentment", "Joy")

  var odf = spark.read.parquet(complete)
  println(odf.rdd.getNumPartitions)
  
  def transform(){
    var df = spark.read.parquet(complete)
    
    // Fix Structure of Parquet DF
    df.drop(df.col("liwc_fwords"))
    df = df.withColumn("liwc_fwords", lit(""))
    
    df.drop(df.col("ext_fwords"))
    df = df.withColumn("ext_fwords", lit(""))
    this.savePAR(df, complete2)
     
   }
   def execute(tokens: Seq[String]): String = {
     val ndf = process(tokens)
     return convertToJson(ndf)
   }
   def process2(tokens: Seq[String]): DataFrame = {
    var df = odf
    val a6 = Literal.create(tokens, ArrayType(StringType, containsNull = false))
    df = df.withColumn("liwc_fwords", array_intersect(typedLit(tokens),col("liwc_words")))
    df = df.withColumn("ext_fwords", array_intersect(typedLit(tokens),col("ext_words")))
    df = df.withColumn("liwc_count", size(col("liwc_fwords")))
    df = df.withColumn("ext_count", size(col("ext_fwords")))
    df = df.withColumn("union",array_union(col("liwc_fwords"),col("ext_fwords")))
    df = df.withColumn("count",size(col("union")))
    df = df.filter(col("count")>0) 
    df.show()
    return df.select("emotion","union", "count")
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
      val js = out.toJson // Json.toJson(out)
      println(js.toString())
      return js.toString() 
   }
   def convertToJsonPerf(df: DataFrame): String = {
      var res = Seq[EmoData]()
      
      takenTime("Collect",0,{
        df.collect().foreach(r => {
          val e = new EmoData(
              r.getAs[String]("emotion"),
              r.getAs[Integer]("count"),
              r.getAs[Seq[String]]("union"))
          res = res :+ e
        })
      })
      var prime: Seq[EmoData] = null
      takenTime("Prime",0,{
        prime = this.getPrime(res)
      })
      val out = new EmoRes(res,prime)
      val js = out.toJson
      
      return js.toString() 
   }
   def getData(df: DataFrame): String = {
    var res = Seq[EmoData]()
    df.collect().foreach(r => {
      val cnt = r.getAs[Integer]("liwc_count")
      val ecnt = r.getAs[Integer]("ext_count")
      if (cnt > 0) {
        val es = r.getAs[String]("emotion")
        val ws = r.getAs[Seq[String]]("liwc_fwords")
        if (ecnt > 0) {
          val wsext = r.getAs[Seq[String]]("ext_fwords")
          val wsSet = Set(ws: _*)
          val wsExtSet = Set(wsext: _*)
          val int = wsSet.intersect(wsExtSet)
          val left = wsExtSet.diff(int)
          if (left.size > 0) {
            //println(left)
            val ncnt = cnt + left.size
            val nws: Seq[String] = ws ++ left.toSeq
            val e = new EmoData(es, ncnt, nws)
            res = res :+ e
          } else {
            val e = new EmoData(es, cnt, ws)
            res = res :+ e
          }
        } else {
          val e = new EmoData(es, cnt, ws)
          res = res :+ e
        }
      }
      if (cnt == 0) {
        if (ecnt > 0) {
          val es = r.getAs[String]("emotion")
          val ws = r.getAs[Seq[String]]("ext_fwords")
          val e = new EmoData(es, ecnt, ws)
          res = res :+ e
        }
      }
    })
    println(res)
    println("end")
   
    var prime: Seq[EmoData] = null
    takenTime("Prime",0,{
      prime = this.getPrime(res)
    })
    val out = new EmoRes(res,prime)
    val js = out.toJson //Json.toJson(out)
    return js.toString()
  }
  def getPrime(emo: Seq[EmoData]): Seq[EmoData] = {
    if (emo.isEmpty) return Seq[EmoData](new EmoData("Unknown",0,Seq[String]()))
    val max = emo.maxBy(f => f.count)
    val res = emo.filter(p => p.count == max.count)
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

object EmoSimple extends App with Performance {
  println("Starting EmoSimple")
  //val tokens = List("terrible","funny","story","peacefully","foolproof","amaze","forgive")
  //var tokens = List("north", "american", "electric", "reliability", "council", "-lrb-", "nerc", "-rrb-", "december", "4", ",", "2001", ",", "posted", "public", "comment", "proposed", "wholesale", "electric", "standards", "model", "-lrb-", "wesm", "-rrb-", "--", "industry-based", "consensus", "process", "developing", ",", "maintaining", ",", "publishing", "standards", "promote", "reliable", "efficient", "wholesale", "electricity", "markets", "throughout", "north", "america", ".", "standards", "would", "address", ",", "integrated", "way", ",", "whole", "spectrum", "reliability", ",", "market", "interface", ",", "business", "practice", "standards", "fair", ",", "open", ",", "balanced", ",", "inclusive", "stakeholder", "process", ".", "december", "19", ",", "federal", "energy", "regulatory", "commission", "-lrb-", "ferc", "-rrb-", "issued", "order", "providing", "guidance", "formation", "standards", "development", "organization", "wholesale", "electric", "industry", "-lrb-", "docket", ".", "rm01-12-00", "-rrb-", ".", "order", "directs", "industry", "reach", "agreement", "formation", "organization", "develop", "consensus", "standards", "business", "practices", "electronic", "communications", ".", "industry", "reach", "consensus", "issue", "march", "15", ",", "commission", "indicated", "either", "institute", "procedures", "choose", "organization", "develop", "standards", ".", "provide", "nerc", "standing", "committees", "representation", "task", "force", "-lrb-", "scrtf", "-rrb-", "well", "nerc", "stakeholders", "committee", "board", "trustees", "broadest", "possible", "input", ",", "board", "strongly", "urges", "interested", "parties", "comment", "wesm", "proposal", "context", "recent", "ferc", "order", ".", "commenters", "should", "give", "particular", "attention", "nerc", "recently", "approved", "north", "american", "energy", "standards", "board", "-lrb-", "naesb", "-rrb-", "could", "collaborate", "developing", "overseeing", "single", ",", "industry-based", "consensus", "process", "develop", "standards", "assure", "continued", "reliable", "operation", "integrated", "north", "american", "electric", "transmission", "grids", "well", "development", "business", "practice", "standards", "communication", "protocols", "needed", "complement", "market", "design", "principles", "ferc", "announced", "intention", "develop", ".", "nerc", "'s", "scrtf", "use", "comments", "received", "help", "shape", "final", "proposal", "nerc", "stakeholders", "committee", "board", "trustees", "february", "2002", ".", "also", "intend", "provide", "input", "receive", "public", "forum", "wesm", "model", "ferc", "consideration", ".", "additional", "information", "scrtf", "membership", ",", "go", ":", "www.nerc.com/committees/scrtf.html", ".", "please", "direct", "questions", "david", "nevius", ",", "nerc", "vice", "president", ",", "609-452-8060", ",", "e-mail", "dave.nevius@nerc.com", ".", "proposal", "posted", "following", "nerc", "web", "site", ":", "http://www.nerc.com/", ".", "deadline", "comments", "january", "8", ",", "2002", ".", "sincerely", ",", "heather", "gibbs")
  val tokens = List("hi", "folks", ",", "do", "not", "erase", ",", "this", "is", "a", "hoax", ".", "according", "to", "symantac", ".", "Consult", "others", "before", "u", "decide", "to", "erase", "this", "crucial", "part", "to", "start", "windows", ".", "have", "a", "good", "2002", "thunder", "wrote", ":", ">", "Hi", ",", "I", "am", "sorry", "to", "have", "to", "tell", "you", "this", "but", "2001", "refuses", "to", "go", "out", "quitely", ">", "for", "allof", "us", ".", ">", ">", "Someone", "who", "had", "us", "in", "their", "address", "book", "infected", "our", "computer", "with", "a", ">", "nasty", ">", ">", "virus", ".", "It", "can", "be", "transferred", "to", "all", "in", "our", "address", "book", "--", "and", "you", "happen", ">", "to", ">", ">", "be", "in", "our", "address", "book", ".", "We", "caught", "it", "before", "it", "did", "any", "damage", "to", "our", ">", ">", "computer", ".", ">", ">", ">", ">", "We", "received", "the", "message", "below", ",", "checked", "it", "out", "and", "found", "out", "we", "had", "the", ">", "virus", ">", ">", "in", "the", "C", "drive", ".", "You", "are", "in", "our", "address", "book", ",", "so", "please", "check", "you", "computer", ">", ">", "for", "the", "virus", ".", ">", ">", ">", ">", "The", "virus", "lies", "dormant", "for", "14", "days", "and", "then", "kills", "your", "hard", "drive", ".", "Here", "'s", ">", ">", "how", "to", "stop", "it", ".", "If", "you", "'ve", "got", "it", ",", "send", "this", "to", "everyone", "in", "your", "address", ">", "book", ".", ">", ">", ">", ">", "Remove", "it", "by", "following", "these", "steps", ":", ">", ">", "1", ".", "Go", "to", "``", "start", "''", "then", "to", "``", "find", "or", "search", "'", "-LRB-", "depending", "on", "your", "computer", "-RRB-", ">", ">", "2", ".", "In", "the", "``", "search", "for", "files", "or", "folders", "''", "type", "``", "sulfnbk.exe", "''", "-", "this", "is", "the", ">", ">", "virus", ">", ">", "3", ".", "In", "the", "``", "look", "in", "''", "make", "sure", "you", "'re", "searching", "drive", "C", ">", ">", "4", ".", "Hit", "``", "search", "or", "find", "''", ">", ">", "5", ".", "If", "this", "file", "shows", "up", "-LRB-", "it", "'s", "an", "ugly", "blackish", "icon", "that", "will", "have", "the", ">", "name", ">", ">", "``", "sulfnbk.exe", ">", ">", "DO", "NOT", "OPEN", "IT", "!!", ">", ">", "6", ".", "Right", "click", "on", "the", "file", "-", "go", "down", "to", "delete", "and", "left", "click", ">", ">", "7", ".", "It", "will", "ask", "you", "if", "you", "want", "to", "send", "it", "to", "the", "recycle", "bin", ",", "say", "yes", ">", ">", "8", ".", "Go", "to", "your", "desktop", "-LRB-", "where", "all", "of", "your", "icons", "are", "-RRB-", "and", "double", "click", "on", ">", ">", "recycle", "bin", ">", ">", "9", ".", "Right", "click", "on", "``", "sulfnbk.exe", "''", "and", "delete", "again", "-", "or", "empty", "bin", ">", ">", ">", ">", "If", "you", "find", "it", ",", "send", "the", "e-mail", "to", "all", "in", "your", "address", "book", "because", ">", "that", "'s", ">", ">", "how", "it", "is", "transferred", ".")

  println(tokens.length)
//  for(i <- 0 to 500){
//    tokens = tokens.+:("interest")
//  }
  println(tokens.length)
  var o = new EmoSimple()
  var res = ""
  averageTime("process",{
    val ndf = o.process2(tokens)
    res = o.convertToJson(ndf)
  },1)
//  takenTime("getData",{
//    o.getData(ndf)
//  })
  println("json string",res)
}
