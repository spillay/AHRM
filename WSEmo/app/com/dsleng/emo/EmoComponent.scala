package com.dsleng.emo



import javax.inject.Inject
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import org.apache.spark.sql.{Row, SparkSession}
import com.dsleng.emo.helper.SparkHelper
import play.api.libs.json.Json
import play.api.libs.json._

trait EmoComponent {
  def InitSpark: Unit
  def execute(tokens: Seq[String]): String
  def Entropy(emotions: JsValue): String
}

class EmoComponentImpl @Inject()(lifecycle: ApplicationLifecycle) extends EmoComponent with SparkHelper {
  override def InitSpark(): Unit = {
    println("StartUp")
    this.setup()
  }
  override def execute(tokens: Seq[String]): String ={
    //this.processTokens(tokens).show()
    //this.processExtTokens(tokens).show()
    return this.process(tokens)
  }
  override def Entropy(emotions: JsValue): String ={
    return this.getEntropy(emotions)
  }
  def start() = InitSpark()
  
  lifecycle.addStopHook { () =>
    println("Shutting Down")
    this.close()
    Future.successful(())
  }
  
  start()
}