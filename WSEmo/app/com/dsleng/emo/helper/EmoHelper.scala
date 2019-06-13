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
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.IndexedSeq
import scala.util.control.Breaks._

case class Emo(emotion: String, words: Seq[String])
case class EmoData(emotion: String, cnt: Integer, words: Seq[String])
object EmoData {
  implicit val implicitEDWrites = new Writes[EmoData] {
    def writes(o: EmoData): JsValue = {
      Json.obj(
        "emotion" -> o.emotion,
        "count" -> o.cnt.toInt,
        "words" -> o.words)
    }
  }
}
case class EmoRes(emotions: Seq[EmoData],prime: Seq[EmoData])
object EmoRes {
  implicit val implicitResWrites = new Writes[EmoRes] {
    def writes(o: EmoRes): JsValue = {
      Json.obj(
        "emotions" -> o.emotions,
        "prime" -> o.prime
       )
    }
  }
}

class EmoHelper(val spark: SparkSession) {
  import spark.sqlContext.implicits._

  val path = "/Data/emo-store/dict-data/twelve_emotions_liwc.json"
  val extpath = "/Data/emo-store/dict-data/extended_emotions.pkl.json"
  val output = "/Data/emo-store/dict-data/twelve_emotions_liwc-df.parquet"
  val output2 = "/Data/emo-store/dict-data/extended_emotions.pkl.parquet"
  val emoOrder = Array("Shame", "Fear", "Anger", "Disgust", "Sadness", "Anxiety", "Relief", "Pride", "Interest", "Agreeableness", "Contentment", "Joy")

  val onlyflatten = udf((list: Seq[Seq[String]]) => { list.flatten })
  def compareStrAgainstArray() = udf((str: String, lst: Seq[String], fwords: Seq[String]) =>
    Transformers.compareStrAgainstArray(str, lst, fwords))
  def compareStrArray() = udf((lst1: Seq[String], lst2: Seq[String]) =>
    Transformers.compareStrArrays(lst1, lst2))
  def countArr() = udf((lst: Seq[String]) =>
    Transformers.countArr(lst))

  println("before reading")
  val df_extended = spark.sqlContext.read.parquet(output2).as("ext")
  val df = spark.sqlContext.read.parquet(output).as("liwc")  
  //var df = TranslateStruct(df_liwc)
  
  def processExt(tokens: Seq[String]): DataFrame = {
    var ndf = df_extended
    tokens.foreach(tok => {
      ndf = ndf.withColumn("count", compareStrAgainstArray()(lit(tok), col("words"), col("count")))
    })
    return ndf
  }

  def processLIWC(tokens: Seq[String]): DataFrame = {
    var ndf = df
    tokens.foreach(tok => {
      ndf = ndf.withColumn("count", compareStrAgainstArray()(lit(tok), col("words"), col("count")))
    })
    return ndf
  }
  def process(tokens: Seq[String]): DataFrame = {
    var ndf = df.join(df_extended, col("ext.emotion") === col("liwc.emotion")).
      select(
        col("liwc.emotion") as "emotion",
        col("liwc.words") as "liwc_words",
        col("liwc.count") as "liwc_count",
        col("liwc.fwords") as "liwc_fwords",
        col("ext.fwords") as "ext_fwords",
        col("ext.count") as "ext_count",
        col("ext.words") as "ext_words")
        
    tokens.foreach(tok => {
      ndf = ndf.withColumn("liwc_fwords", compareStrAgainstArray()(lit(tok), col("liwc_words"), col("liwc_fwords")))
      ndf = ndf.withColumn("ext_fwords", compareStrAgainstArray()(lit(tok), col("ext_words"), col("ext_fwords")))
    })

    ndf = ndf.withColumn("ext_count", countArr()(col("ext_fwords")))
    ndf = ndf.withColumn("liwc_count", countArr()(col("liwc_fwords")))
    
    ndf.show()
    return ndf
  }
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
    val js = Json.toJson(out)
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