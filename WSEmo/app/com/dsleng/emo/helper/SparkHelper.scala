package com.dsleng.emo.helper

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame
import play.api.libs.json._

trait SparkHelper{
  def appName: String = "Emotion Analysis"
  //def appName_=(s: String): Unit
  def master: String = "local[*]"
  var spark: SparkSession = null
  var emo: EmoHelper = null
  var ent: EntropyHelper = null
  
  def setup() {
     println(s"Starting Up SparkSession for $appName")
     spark = SparkSession
      .builder
      .appName(appName)
      .master(master)
      .config("spark.eventLog.enabled", "true")
      .config("spark.eventLog.dir", "file:/usr/local/spark/history/")
      .config("spark.sql.codegen.wholeStage","false")
      .getOrCreate()
      
     //spark.conf.set("spark.eventLog.enabled", "true").set("spark.eventLog.dir", "file:///C:/Users/me/spark/logs")
     //spark.sparkContext.setLogLevel("ERROR")
      emo = new EmoHelper(spark)
      ent = new EntropyHelper(spark) 
     
  }
  def processTokens(tokens: Seq[String]): DataFrame ={
    return emo.processLIWC(tokens)
  }
   def processExtTokens(tokens: Seq[String]): DataFrame ={
    return emo.processExt(tokens)
  }
   def process(tokens: Seq[String]): String ={
    val df = emo.process(tokens)
    return emo.getData(df)
  }
  def getEntropy(data: JsValue): String={
    return ent.process(data)
  } 
  def close() {
    spark.close()
  }
  def saveCSV(df: DataFrame,output: String){
      val tsvWithHeaderOptions: Map[String, String] = Map(
        ("delimiter", "\t"), // Uses "\t" delimiter instead of default ","
        ("header", "true"))  // Writes a header record with column names
      
      df.coalesce(1)         // Writes to a single file
        .write
        .mode(SaveMode.Overwrite)
        .options(tsvWithHeaderOptions)
        .csv("output/path")
        
     
    
  }
  def savePAR(df: DataFrame,output: String){
     df.write.parquet(output)
  }
  def loadCSV(name: String, header: Boolean): DataFrame = {
    val df = spark.read.format("csv")
      .option("sep", ",")
      .option("multiLine", true)
      .option("inferSchema", "true")
      .option("header", header)
      .load(name)
    return df;
  }
  def loadJSON(name: String): DataFrame = {
    return spark.sqlContext.read.option("multiLine", true).option("mode", "PERMISSIVE").json(name)
    //return spark.sqlContext.read.json(name)
  }
}