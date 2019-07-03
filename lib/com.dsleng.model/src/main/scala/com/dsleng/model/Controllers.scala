package com.dsleng.model

import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.dsleng.email.ModelJsonImplicits._

case class EmoEmailCtl(model: SimpleEmailExt,emotion: String)
case class EmailCtl(model: SimpleEmailExt)
case class TextCtl(text: String)
case class TokenCtl(model: SimpleEmailExt,tokens: TokenStrCtl)
case class TokenStrCtl(tokens: List[String])
case class NormData(emotion: String, count: Double)
case class NLPDataCtl(tokensSW: Seq[String],sw: Seq[String],tokens: Seq[String])
case class TokenDataCtl(model: SimpleEmailExt,nlp: NLPDataCtl)

object CtlJsonImplicits extends DefaultJsonProtocol {
  implicit val impTokenStrCtl = jsonFormat1(TokenStrCtl)
  implicit val impTokenCtl = jsonFormat2(TokenCtl)
  implicit val impEmoEmailCtl = jsonFormat2(EmoEmailCtl)
  implicit val impNormData = jsonFormat2(NormData)
}

case class Emo(emotion: String, words: Seq[String])
case class EmoData(emotion: String, count: Integer, words: Seq[String])

object NLPDataCtl {
  implicit val tdJsonFormat = new RootJsonFormat[NLPDataCtl] {
    def write(o: NLPDataCtl): JsValue = {
      println("writing emodata")
      JsObject(
        "sw" -> JsArray(o.sw.map(JsString(_)).toVector),
        "tokens" -> JsArray(o.tokens.map(JsString(_)).toVector),
        "tokensSW" -> JsArray(o.tokensSW.map(JsString(_)).toVector)
       )
    }
    def read(value: JsValue) = {
      value.asJsObject.getFields("sw","tokens","tokensSW") match {
        case Seq(JsArray(sw),JsArray(tokens),JsArray(tokensSW)) => 
          new NLPDataCtl(
              tokensSW.map(_.toString()),
              sw.map(_.toString()),
              tokens.map(_.toString())
              )
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}


object TokenDataCtl {
  implicit val tdJsonFormat = new RootJsonFormat[TokenDataCtl] {
    def write(o: TokenDataCtl): JsValue = {
      println("writing emodata")
      JsObject(
        "model" -> o.model.toJson,
        "nlp" -> o.nlp.toJson
       )
    }
    def read(value: JsValue) = {
      value.asJsObject.getFields("model","nlp") match {
        case Seq(JsString(model),JsString(nlp)) => 
          new TokenDataCtl(
              model.parseJson.convertTo[SimpleEmailExt],
              nlp.parseJson.convertTo[NLPDataCtl]
              )
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}


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