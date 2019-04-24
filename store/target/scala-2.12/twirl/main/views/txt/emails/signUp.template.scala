
package views.txt.emails

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.txt._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._
/*1.2*/import play.api.i18n.Messages

object signUp extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.TxtFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.TxtFormat.Appendable]](play.twirl.api.TxtFormat) with _root_.play.twirl.api.Template3[com.dsleng.models.security.User,String,Messages,play.twirl.api.TxtFormat.Appendable] {

  /**/
  def apply/*3.2*/(user: com.dsleng.models.security.User, url: String)(implicit messages: Messages):play.twirl.api.TxtFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*4.2*/messages("email.sign.up.hello", user.username)),format.raw/*4.48*/("""

"""),_display_(/*6.2*/messages("email.sign.up.txt.text", url)),format.raw/*6.41*/("""
"""))
      }
    }
  }

  def render(user:com.dsleng.models.security.User,url:String,messages:Messages): play.twirl.api.TxtFormat.Appendable = apply(user,url)(messages)

  def f:((com.dsleng.models.security.User,String) => (Messages) => play.twirl.api.TxtFormat.Appendable) = (user,url) => (messages) => apply(user,url)(messages)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Tue Apr 23 19:32:23 EDT 2019
                  SOURCE: /Data/2019/git/AHRM/store/app/views/emails/signUp.scala.txt
                  HASH: 7ee8e9b1c08b4c389cd12787d2c814c121864145
                  MATRIX: 437->1|809->33|983->116|1049->162|1077->165|1136->204
                  LINES: 17->1|22->3|27->4|27->4|29->6|29->6
                  -- GENERATED --
              */
          