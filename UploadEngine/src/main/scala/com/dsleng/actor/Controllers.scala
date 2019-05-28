package com.dsleng.actor


import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import play.api.libs.json._

case class EmoEmailCtl(model: SimpleEmailExt,emotion: String)
case class EmailCtl(model: SimpleEmailExt)
case class TextCtl(text: String)
case class TokenCtl(model: SimpleEmailExt,tokens: String)

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
object TokenCtl {
  implicit val tokenWrites = Json.writes[TokenCtl]
}