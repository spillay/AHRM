package com.dsleng.emo.helper

import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.dsleng.email.ModelJsonImplicits._

case class EmoEmailCtl(model: SimpleEmailExt,emotion: String)
case class EmailCtl(model: SimpleEmailExt)
case class TextCtl(text: String)
case class TokenCtl(model: SimpleEmailExt,tokens: TokenStrCtl)
case class TokenStrCtl(tokens: List[String])

object CtlJsonImplicits extends DefaultJsonProtocol {
  implicit val impTokenStrCtl = jsonFormat1(TokenStrCtl)
  implicit val impTokenCtl = jsonFormat2(TokenCtl)
  implicit val impEmoEmailCtl = jsonFormat2(EmoEmailCtl)
}

case class Emo(emotion: String, words: Seq[String])
case class EmoData(emotion: String, count: Integer, words: Seq[String])
object EmoData {
  implicit val emJsonFormat = new RootJsonFormat[EmoData] {
    def write(o: EmoData): JsValue = {
      println("writing emodata")
      JsObject(
        "emotion" -> JsString(o.emotion),
        "count" -> JsNumber(o.count),
        "words" -> JsArray(o.words.map(JsString(_)).toVector)
       )
    }
    def read(value: JsValue) = {
      println("reading emodata",value.asJsObject.getFields("emotion"))
      println("reading emodata",value.asJsObject.getFields("count"))
      println("reading emodata",value.asJsObject.getFields("words"))
      value.asJsObject.getFields("emotion","count","words") match {
        case Seq(JsString(emotion),JsNumber(cnt),JsArray(words)) => 
          println(emotion,cnt,words)
          new EmoData(
              emotion,
              cnt.toInt,
              words.map(_.toString()))
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}
case class EmoRes(emotions: Seq[EmoData],prime: Seq[EmoData])
object EmoRes {
  implicit val implicitResWrites = new RootJsonFormat[EmoRes] {
    def write(o: EmoRes): JsValue = {
      println("writing")
      JsObject(
        "emotions" -> JsArray(o.emotions.map(_.toJson).toVector),
        "prime" -> JsArray(o.prime.map(_.toJson).toVector)
       )
    }
    def read(value: JsValue) = {
      println("reading")
      value.asJsObject.getFields("emotions","prime") match {
        case Seq(JsArray(emotion),JsArray(prime)) => 
          new EmoRes(
              emotion.map(_.convertTo[EmoData]),
              prime.map(_.convertTo[EmoData]))
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}