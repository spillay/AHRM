package com.dsleng.emo.helper

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.DataFrame
import play.api.libs.json._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SPFunctions.addOneCustomNative
import org.apache.spark.sql.SPFunctions.checkforFeature
import org.apache.spark.sql.SPFunctions.checkforFeature2

import com.dsleng.tut.QuasiTest
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

trait TestHelper extends SparkHelper with Performance{
  import spark.sqlContext.implicits._
  val testpath = "/Data/emo-store/test-df.parquet"
  val complete = "/Data/emo-store/dict-data/emo-df.parquet"
  val complete2 = "/Data/emo-store/dict-data/emo-dfv2.parquet"
  private def test3(){
      val data = Seq((0.0,0.4,0.4,0.0),(0.1,0.0,0.0,0.7),(0.0,0.2,0.0,0.3),(0.3,0.0,0.0,0.0))
      val cols = Array("p1", "p2", "p3", "p4","index")
      
      var df = data.zip(cols).map { 
        case (col,index) => (col._1,col._2,col._3,col._4,index)
      }.toDF(cols: _*)
      df = df.withColumn("p6", SimpleUDF.test2(col("index")))
      //df.explain(true)
      val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
      df.queryExecution.debug.codegen()
      averageTime("s",{ df.count() })
      //df.write.parquet(testpath)
  }
  def test5(){
    var df = spark.read.parquet(testpath)
    df = df.withColumn("p6", addOneCustomNative(lit(2)))
    df.explain(true)
    val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
    averageTime("s",{ df.count() })
    df.show()
  }
  def test4(){
    val df = spark.read.parquet(complete)
    //val ndf = df.withColumn("nemotion", SimpleUDF.test2(col("emotion")))
    val ndf = df.withColumn("nemotion", col("emotion"))
    ndf.explain()
    //averageTime {ndf.show()}
  }
  def test6(){
    val a6 = Literal.create(Seq("hello", "sure*", "bye"), ArrayType(StringType, containsNull = false))
    val sa = SPArrayContains2(a6, Literal("surest"))
    
    val ctx = new CodegenContext()
    val expr = sa.genCode(ctx)
    println(expr)
    QuasiTest.printTree("simple"){expr.code}
  }
  def test7(){
    
    val a6 = Literal.create(Seq("hello", "sure*", "bye"), ArrayType(StringType, containsNull = false))
    val sa = SPArrayContains(a6, Literal("surest"),Literal(""))
    
    println("Literal Null",Literal("").nullable)
    println("Array",a6.nullable)
    println("Array Contains",a6.dataType.asInstanceOf[ArrayType].containsNull)
    
    val ctx = new CodegenContext()
    val expr = sa.genCode(ctx)
    println(expr)
    QuasiTest.printTree("simple"){expr}
    
    val serializer = new JavaSerializer(spark.sparkContext.getConf).newInstance
    val resolver = ResolveTimeZone(new SQLConf)
    val expr2 = resolver.resolveTimeZones(serializer.deserialize(serializer.serialize(sa)))
    println(expr2)
    val result = expr2.eval()
    println(result)
  }
   def test8(){
     println(spark.sessionState.conf.wholeStageEnabled)
      var data = Seq(Seq("hello","bye","welcome"),Seq("hello","bye","welcome"),Seq("hello","bye","welcome"),Seq("hello","bye","welcome"))
      
      println(data.length)
      val cols = Array("p1","index")
      val a6 = Literal.create(Seq("hello", "sure*", "bye"), ArrayType(IntegerType, containsNull = false))
      
      var df = data.zip(cols).map { 
        case (col,index) => (col,index)
      }.toDF(cols: _*)
//      for(i <- 0 to 3){
//        val df2 = df.withColumn("p1", typedLit(Seq("whello","bye","welcome"))).withColumn("index", lit(i))
//        df = df.union(df2)
//        println(i)
//      }
//      println("before show")
      
      
      //df = df.withColumn("p2", checkforFeature(col("p1"),lit("hello")))
      df.show(true)
      df = df.where('p1 !== typedLit(Seq()))
      val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
      
      //df.queryExecution.debug.codegen()
      //df.debugCodegen()
      df.explain(false)
      averageTime("s",{ df.count() })
      df.show();
   }
   def test9(){
     var df =  spark.range(1000L * 1000 * 1000).selectExpr("sum(id)")
     df.explain(false)
     val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
     df.show()
   }
   def test10(){
     var df = spark.read.parquet(complete)
     //df = df.where('liwc_count === 0)
     df = df.withColumn("words", lit(""))
     
     df = df.withColumn("words", checkforFeature(col("liwc_words"),lit("abandoned"),col("words")))
     df = df.withColumn("words", checkforFeature(col("liwc_words"),lit("ache"),col("words")))

     df = df.withColumn("words", checkforFeature(col("liwc_words"),lit("ac"),col("words")))

     
     
       df = df.withColumn("words", checkforFeature(col("liwc_words"),lit("afraid"),col("words")))
       df = df.withColumn("words", checkforFeature(col("liwc_words"),lit("danger"),col("words")))
       df = df.withColumn("words", checkforFeature(col("liwc_words"),lit("adoration"),col("words")))

     df.show()
     
     df.explain()
     val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
   }
    private def test11(){
      var df = spark.read.parquet(complete2)
      var edf = spark.read.parquet(complete2)
      //df.show()
      //df.drop(df.col("liwc_fwords"))
      //df = df.withColumn("liwc_fwords",lit(null).cast("string"))
      df.show()
      //df = df.withColumn("liwc_fwords", checkforFeature(col("liwc_words"),lit("abandoned"),col("liwc_fwords")))
      takenTime("df",{
        df = df.withColumn("liwc_fwords", checkforFeature(col("liwc_words"),lit("abandoned"),col("liwc_fwords")))
        df = df.withColumn("liwc_fwords", checkforFeature(col("liwc_words"),lit("ache"),col("liwc_fwords")))
        df = df.withColumn("liwc_fwords", checkforFeature(col("liwc_words"),lit("ac"),col("liwc_fwords")))
      })
      takenTime("df2",{
        df = df.filter(col("liwc_fwords")!=="")
      })
      takenTime("df3",{
        df = df.withColumn("liwc_count", size(split(col("liwc_fwords"), ",").cast("array<string>")))
      })
  //    takenTime{
  //      df = df.withColumn("liwc_array", split(col("liwc_fwords"), ",").cast("array<string>"))
  //      df = df.withColumn("liwc_count", size(col("liwc_array")))
  //    }
      df.show()
      df = df.select(col("emotion"), col("liwc_count"),col("liwc_fwords"))
        .joinWith(edf, df.col("emotion")===edf.col("emotion")).toDF()
    
  }
   
}