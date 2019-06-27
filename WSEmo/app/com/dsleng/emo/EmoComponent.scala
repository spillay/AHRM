package com.dsleng.emo



import javax.inject.Inject
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import org.apache.spark.sql.{Row, SparkSession}
import com.dsleng.emo.helper.Emotions
import play.api.libs.json.Json
import play.api.libs.json._

trait EmoComponent {
  def InitSpark: Unit
  def execute(tokens: Seq[String]): String
  //def Entropy(emotions: JsValue): String
}

class EmoComponentImpl @Inject()(lifecycle: ApplicationLifecycle) extends EmoComponent with Emotions {
  override def InitSpark(): Unit = {
    println("StartUp")
    //this.setup()
  }
  override def execute(tokens: Seq[String]): String ={
    return this.execute(tokens)
  }
  def start() = InitSpark()
  
  lifecycle.addStopHook { () =>
    println("Shutting Down")
    this.close()
    Future.successful(())
  }
  
  start()
}