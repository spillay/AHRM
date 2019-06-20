package com.dsleng.emo

import com.dsleng.emo.helper._
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
import org.apache.spark.sql.types._
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenContext
import org.apache.spark.sql.execution.command.ExplainCommand
import org.apache.spark.sql.execution.debug._
import org.apache.spark.sql.functions.typedLit
import org.apache.spark.sql.functions.array_contains

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
      //df.explain(true)
      val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
      df.queryExecution.debug.codegen()
      averageTime{ df.count() }
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
  def test6(){
    val a6 = Literal.create(Seq("hello", "sure*", "bye"), ArrayType(StringType, containsNull = false))
    val sa = SPArrayContains(a6, Literal("surest"))
    
    val ctx = new CodegenContext()
    val expr = sa.genCode(ctx)
    println(expr)
    QuasiTest.printTree("simple"){expr}
  }
  def test7(){
    
    val a6 = Literal.create(Seq("hello", "sure*", "bye"), ArrayType(StringType, containsNull = false))
    val sa = SPArrayContains2(a6, Literal("surest"),Literal(""))
    
    println("Literal Null",Literal("").nullable)
    println("Array",a6.nullable)
    println("Array Contains",a6.dataType.asInstanceOf[ArrayType].containsNull)
    
    val ctx = new CodegenContext()
    val expr = sa.genCode(ctx)
    println(expr)
    QuasiTest.printTree("simple"){expr}
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
      averageTime{ df.count() }
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
     df = df.withColumn("words", checkforFeature2(col("liwc_words"),lit("abandoned"),col("words")))
     df = df.withColumn("words", checkforFeature2(col("liwc_words"),lit("ache"),col("words")))
     df.show()
     df.explain()
     val explain = ExplainCommand(df.queryExecution.logical, codegen=true)
      spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
        r => println(r.getString(0))
      }
   }
}

object EmoSimple extends App {
  println("Starting EmoSimple")
  //val func = function.asInstanceOf[() => Any]
  val tokens = List("terrible","funny","story")
  /*
  val qt = QuasiTest.addCreationDate()
  println(qt)
  println(QuasiTest.greeting)
  QuasiTest.printTree("treet")(123)
  QuasiTest.printTree(""){
    def myMethod = {
      val a = 1
      def b = 2
      assert(a == b)
    }
  }
  */
  var o = new EmoSimple()
  o.test7()
  //o.test3()
  //o.test4()
  o.test10()
  //o.test9()
  //o.processTest(tokens)
}