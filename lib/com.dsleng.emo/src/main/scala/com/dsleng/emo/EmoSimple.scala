package com.dsleng.emo

import com.dsleng.emo.helper._
import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame
import play.api.libs.json._
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.SPFunctions.addOneCustomNative

class EmoSimple extends SparkHelper with Performance {
  import spark.sqlContext.implicits._
  
  val testpath = "/Data/emo-store/test-df.parquet"
  val complete = "/Data/emo-store/dict-data/emo-df.parquet"
  
  def test3(){
      val data = Seq((0.0,0.4,0.4,0.0),(0.1,0.0,0.0,0.7),(0.0,0.2,0.0,0.3),(0.3,0.0,0.0,0.0))
      val cols = Array("p1", "p2", "p3", "p4","index")
      
      var df = data.zip(cols).map { 
        case (col,index) => (col._1,col._2,col._3,col._4,index)
      }.toDF(cols: _*)
      df = df.withColumn("p6", SimpleUDF.test2(col("index")))
      df.explain(true)
      averageTime{ df.count() }
      //df.write.parquet(testpath)
  }
  def test5(){
    var df = spark.read.parquet(testpath)
    df = df.withColumn("p6", addOneCustomNative(lit(1)))
    df.explain(true)
    averageTime{ df.count() }
    df.show()
  }
  def test4(){
    val df = spark.read.parquet(complete)
    //val ndf = df.withColumn("nemotion", SimpleUDF.test2(col("emotion")))
    val ndf = df.withColumn("nemotion", col("emotion"))
    ndf.explain()
    //averageTime {ndf.show()}
  }
}

object EmoSimple extends App {
  println("Starting EmoSimple")
  //val func = function.asInstanceOf[() => Any]
  val tokens = List("terrible","funny","story")
  
  var o = new EmoSimple()
  //o.test3()
  //o.test4()
  o.test5()
  //o.processTest(tokens)
}