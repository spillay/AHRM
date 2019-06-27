package com.dsleng.model

import java.sql.Timestamp
import java.sql.Timestamp
import java.text.SimpleDateFormat
//import com.dsleng.model.utils.SPImplicits._
//import com.dsleng.nlp.SimplePL
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import spray.json._
/*
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
 // implicit val emReads = Json.reads[EmailModel]
//  implicit val emWrites = new Writes[EmailModel] {
//    def writes(a: EmailModel) = Json.obj(
//      "From" -> a.from,
//      "To" -> a.to,
//      "Date" -> a.getDate(),
//      "Subject" -> a.subject,
//      "Content" -> a.content,
//      "RefContent" -> a.ref_content,
//      "TOKS" -> a.getToks(),
//      "NER" -> a.getNER(),
//      "POS" -> a.getPos(),
//      "Topic" -> a.getTopic(),
//      "KeyPhrases" -> a.getPhrases(),
//      "Questions" -> a.getQuestions(),
//      "Emotions" -> a.getEmotion(),
//      "WordCount" -> a.getWordCount())
//  }
  implicit val emJsonFormat = new RootJsonFormat[EmailModel] {
    def write(a: EmailModel): JsValue = {
      JsObject(
        "From" -> a.from.parseJson,
        "To" -> a.to.parseJson,
        "Date" -> a.getDate().parseJson,
        "Subject" -> a.subject.parseJson,
        "Content" -> a.content.parseJson,
        "RefContent" -> a.ref_content.parseJson,
        "TOKS" -> a.getToks().parseJson,
        "NER" -> a.getNER().parseJson,
        "POS" -> a.getPos().parseJson,
        "Topic" -> a.getTopic().parseJson,
        "KeyPhrases" -> a.getPhrases().parseJson,
        "Questions" -> a.getQuestions().parseJson,
        "Emotions" -> a.getEmotion().parseJson,
        "WordCount" -> a.getWordCount().parseJson
      )
    }
//    def read(value: JsValue) = {
//      value.asJsObject.getFields( 
//        "From",
//        "To",
//        "Date",
//        "Subject",
//        "Content",
//        "RefContent",
//        "TOKS",
//        "NER",
//        "POS",
//        "Topic",
//        "KeyPhrases",
//        "Questions",
//        "Emotions",
//        "WordCount") match {
//        case Seq( JsString(from),JsString(to),JsString(date),JsString(subject),
//            JsString(content),JsString(refContent),JsString(toks),JsString(ner),JsString(pos),
//            JsString(topic),JsString(keyPhrases),JsString(questions),JsString(emotions),JsString(wordCount)) =>
//          new EmailModel(from,to,date,subject,
//            content,refContent)
//        case _ => throw new DeserializationException("Color expected")
//      }
//    }
    
  }
  
  def getESMapping(): String = {
    val map: JsValue = """
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
    """.parseJson
    return map.toString()
  }

}
* */
