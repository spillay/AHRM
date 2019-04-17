
package views.html.emails

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._
/*1.2*/import play.api.i18n.Messages
/*2.2*/import play.twirl.api.Html

object signUp extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template3[com.dsleng.models.security.User,String,Messages,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*4.2*/(user: com.dsleng.models.security.User, url: String)(implicit messages: Messages):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*5.1*/("""
"""),format.raw/*6.1*/("""<html>
    <body>
        <p>"""),_display_(/*8.13*/messages("email.sign.up.hello", user.username)),format.raw/*8.59*/("""</p>
        <p>"""),_display_(/*9.13*/Html(messages("email.sign.up.html.text", url))),format.raw/*9.59*/("""</p>
    </body>
</html>
"""))
      }
    }
  }

  def render(user:com.dsleng.models.security.User,url:String,messages:Messages): play.twirl.api.HtmlFormat.Appendable = apply(user,url)(messages)

  def f:((com.dsleng.models.security.User,String) => (Messages) => play.twirl.api.HtmlFormat.Appendable) = (user,url) => (messages) => apply(user,url)(messages)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Wed Apr 17 10:37:52 EDT 2019
                  SOURCE: /Data/2019/git/AHRM/store/app/views/emails/signUp.scala.html
                  HASH: 46a6d89f754a8c659ef489e5cecc8c3a0a5e1411
                  MATRIX: 439->1|476->32|849->61|1024->143|1051->144|1107->174|1173->220|1216->237|1282->283
                  LINES: 17->1|18->2|23->4|28->5|29->6|31->8|31->8|32->9|32->9
                  -- GENERATED --
              */
          