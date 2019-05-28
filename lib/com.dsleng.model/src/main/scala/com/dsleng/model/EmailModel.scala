package com.dsleng.model

import java.sql.Timestamp
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }
import java.sql.Timestamp
import java.text.SimpleDateFormat
import play.api.libs.json._
import com.dsleng.model.utils.SPImplicits._
import com.dsleng.nlp.SimplePL
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

case class EmailModel(from: String, to: String, dte: Timestamp, subject: String, content: String,ref_content: String="",ques: ListBuffer[String]=null)  {
  var nlpProc: SimplePL = null
  val output = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
  def getDate(): String = {
    return output.format(dte)
  }
   def getToks(): String = {
    if (nlpProc == null ){
      nlpProc = new SimplePL()
      nlpProc.process(ref_content)
    }
    return "[" + nlpProc.getTokens().asScala.mkString(",") + "]"
  }
  def getNER(): String = {
    if (nlpProc == null ){
      nlpProc = new SimplePL()
      nlpProc.process(ref_content)
    }
    return "[" + nlpProc.getNER().asScala.mkString(",") + "]"
  }
   def getPos(): String = {
    if (nlpProc == null ){
      nlpProc = new SimplePL()
      nlpProc.process(ref_content)
    }
    return "[" + nlpProc.getPOS().asScala.mkString(",") + "]"
  }
  def getTopic(): String = {
    return "['breathing','exercise']"
  }
  def getPhrases(): String = {
    return "['source code','BLE services','couple questions','few questions']"
  }
  def getQuestions(): String = {
    return "[" + ques.mkString(",") + "]"
  }
  def getEmotion(): String = {
    return "{\"Contentment\": 0.75, \"Interest\": 0.25}"
  }
  def getWordCount(): String = {
    return "100";
  }
}

object EmailModel {
  //implicit val emWrites = Json.writes[EmailModel]
  implicit val emReads = Json.reads[EmailModel]
  implicit val emWrites = new Writes[EmailModel] {
    def writes(a: EmailModel) = Json.obj(
      "From" -> a.from,
      "To" -> a.to,
      "Date" -> a.getDate(),
      "Subject" -> a.subject,
      "Content" -> a.content,
      "RefContent" -> a.ref_content,
      "TOKS" -> a.getToks(),
      "NER" -> a.getNER(),
      "POS" -> a.getPos(),
      "Topic" -> a.getTopic(),
      "KeyPhrases" -> a.getPhrases(),
      "Questions" -> a.getQuestions(),
      "Emotions" -> a.getEmotion(),
      "WordCount" -> a.getWordCount())
  }
  def getESMapping(): String = {
    val map: JsValue = Json.parse("""
        {
          "properties" : {
           "From": {
              "type": "text"
            },
            "To": {
              "type": "text"
            },
           "Date": {
              "type": "date",
              "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis"
            },
            "Subject": {
              "type": "text"
            },
            "Content": {
              "type": "text"
            },
            "RefContent": {
              "type": "text"
            },
            "TOKS": {
              "type": "text"
            },
            "NER": {
              "type": "text"
            },
            "POS": {
              "type": "text"
            },
            "Topic": {
              "type": "text"
            },
            "KeyPhrases": {
              "type": "text"
            },
            "Questions": {
              "type": "text"
            },
            "Emotions": {
              "type": "text"
            },
            "WordCount": {
              "type": "integer"
            }
          }
        }
    """)
    return Json.stringify(map)
  }

}
