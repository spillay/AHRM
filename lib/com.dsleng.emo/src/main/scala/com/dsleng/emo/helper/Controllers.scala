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

/*
 * object TokenStrCtl {
  implicit val tokenStrWrites = Json.writes[TokenStrCtl]
}
object EmailCtl {
  implicit val emailWrites = Json.writes[EmailCtl]
  
//  implicit val emailWrites = new Writes[Email] {
//    def writes(a: Email) = Json.obj(
//      "Address" -> a.name)
//  }
}
object TextCtl {
  implicit val textWrites = Json.writes[TextCtl]
}
object TokenStrCtl {
  implicit val tokenStrWrites = Json.writes[TokenStrCtl]
}
object TokenCtl {
  implicit val tokenWrites = Json.writes[TokenCtl]
}
*/