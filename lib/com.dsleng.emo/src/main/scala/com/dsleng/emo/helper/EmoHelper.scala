package com.dsleng.emo.helper

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.Column
import org.apache.spark.sql.Row
import org.apache.spark.rdd.RDD
import org.apache.spark.ml.feature.{ RegexTokenizer, Tokenizer }
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.DataFrame
import scala.collection.mutable.WrappedArray
import scala.collection.mutable.MutableList
import scala.collection.IndexedSeq
import scala.util.control.Breaks._
import spray.json._
import spray.json.DefaultJsonProtocol._



class EmoHelper(val spark: SparkSession) {
  import spark.sqlContext.implicits._

  val path = "/Data/emo-store/dict-data/twelve_emotions_liwc.json"
  val extpath = "/Data/emo-store/dict-data/extended_emotions.pkl.json"
  val output = "/Data/emo-store/dict-data/twelve_emotions_liwc-df.parquet"
  val output2 = "/Data/emo-store/dict-data/extended_emotions.pkl.parquet"
  val emoOrder = Array("Shame", "Fear", "Anger", "Disgust", "Sadness", "Anxiety", "Relief", "Pride", "Interest", "Agreeableness", "Contentment", "Joy")
  val complete = "/Data/emo-store/dict-data/emo-df.parquet"
  val testpath = "/Data/emo-store/test-df.parquet"
   
  def  onlyflatten = udf{list: Seq[String] => list}
  //def onlyflatten = udf((list: Seq[Seq[String]]) => { list.flatten })
  def compareStrAgainstArray() = udf((str: String, lst: Seq[String], fwords: Seq[String]) =>
    Transformers.compareStrAgainstArray(str, lst, fwords))
  def compareStrArray() = udf((lst1: Seq[String], lst2: Seq[String]) =>
    Transformers.compareStrArrays(lst1, lst2))
  def countArr() = udf((lst: Seq[String]) =>
    Transformers.countArr(lst))

  
  
 
  //val df_extended = spark.sqlContext.read.parquet(output2).as("ext")
  //val df = spark.sqlContext.read.parquet(output).as("liwc")  
  val ndf = spark.sqlContext.read.parquet(complete)
  
  def processExt(tokens: Seq[String],df_extended: DataFrame): DataFrame = {
    var ndf = df_extended
    tokens.foreach(tok => {
      ndf = ndf.withColumn("count", compareStrAgainstArray()(lit(tok), col("words"), col("count")))
    })
    return ndf
  }

  def processLIWC(tokens: Seq[String],df: DataFrame): DataFrame = {
    var ndf = df
    tokens.foreach(tok => {
      ndf = ndf.withColumn("count", compareStrAgainstArray()(lit(tok), col("words"), col("count")))
    })
    return ndf
  }
  
  def processConvert(tokens: Seq[String],df: DataFrame,df_extended: DataFrame): DataFrame = {
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
    var ndf = df.join(df_extended, col("ext.emotion") === col("liwc.emotion")).
      select(
        col("liwc.emotion") as "emotion",
        col("liwc.words") as "liwc_words",
        col("liwc.count") as "liwc_count",
        col("liwc.fwords") as "liwc_fwords",
        col("ext.fwords") as "ext_fwords",
        col("ext.count") as "ext_count",
        col("ext.words") as "ext_words")
        
    ndf.show()
    ndf.write.parquet(complete)
    return ndf
  }
  def test2(){
    def strLength(inputString: String): Long = inputString.size.toLong
    val strLengthUdf = udf(strLength _)
    
    
    var df = ndf.select("emotion")
    df.show()
    //tokens.foreach(tok => {
     df = df.withColumn("emotion2", strLengthUdf(col("emotion")))
    //})
    df.show()
  }
  def test3(){
      val data = Seq((0.0,0.4,0.4,0.0),(0.1,0.0,0.0,0.7),(0.0,0.2,0.0,0.3),(0.3,0.0,0.0,0.0))
      val cols = Array("p1", "p2", "p3", "p4","index")
      
      var df = data.zip(cols).map { 
        case (col,index) => (col._1,col._2,col._3,col._4,index)
      }.toDF(cols: _*)
      df = df.withColumn("p5", SimpleUDF.test2(col("index")))
      df.write.parquet(testpath)
  }
  def process2(tokens: Seq[String]): DataFrame = {
      println(spark.conf.get("spark.sql.parquet.filterPushdown"))
      var df = spark.sqlContext.read.parquet(testpath)
      df.createTempView("s1")
      df.withColumn("p6", SimpleUDF.test2(col("index"))).queryExecution.optimizedPlan
      df = df.withColumn("p6", SimpleUDF.test2(col("index")))
      df.explain()
      df.queryExecution.optimizedPlan
      df.show()
      return df;
  }
//  def process(tokens: Seq[String]): DataFrame = {
//    
//    var ndf = df.join(df_extended, col("ext.emotion") === col("liwc.emotion")).
//      select(
//        col("liwc.emotion") as "emotion",
//        col("liwc.words") as "liwc_words",
//        col("liwc.count") as "liwc_count",
//        col("liwc.fwords") as "liwc_fwords",
//        col("ext.fwords") as "ext_fwords",
//        col("ext.count") as "ext_count",
//        col("ext.words") as "ext_words")
//        
//    ndf.show()
//    println("before tokens 1")
//    tokens.foreach(tok => {
//      ndf = ndf.withColumn("liwc_fwords", compareStrAgainstArray()(lit(tok), col("liwc_words"), col("liwc_fwords")))
//      //ndf = ndf.withColumn("ext_fwords", compareStrAgainstArray()(lit(tok), col("ext_words"), col("ext_fwords")))
//    })
//    ndf.show()
//    println("before tokens 2")
//    ndf = ndf.withColumn("ext_count", countArr()(col("ext_fwords")))
//    ndf.show()
//    println("before tokens 3")
//    ndf = ndf.withColumn("liwc_count", countArr()(col("liwc_fwords")))
//    ndf.show()
//    println("before tokens 4")
//    
//    ndf.show()
//    return ndf
//  }
  def getData(df: DataFrame): String = {
    println("running getdata")
    /*
    val sp = """
      {"emotions":[],"prime":[{"emotion":"Unknown","count":0,"words":[]}]}
      """
    return sp
    */
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
   
    val prime = this.getPrime(res)
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

  def emomatch(str: String, to: String): Boolean = {
    if (to.contains('*')) {
      val nto = to.substring(0, to.indexOf('*'))
      if (str.length() >= nto.length()) {
        val nstr = str.substring(0, nto.length())
        if (nstr.matches(nto)) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    } else {
      return str.matches(to)
    }
    return false;
  }

  def TranslateStruct(df: DataFrame): DataFrame = {
    var nr = MutableList[Emo]()
    df.columns.foreach(c => {
      df.select(c).collect().foreach(r => {
        val arr = r.getAs[Seq[String]](0)
        nr += Emo(c, arr)
      })
    })
    return nr.toDF()
  }
}