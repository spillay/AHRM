
package views.html

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

object welcome extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template2[String,String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(message: String, style: String = "scala"):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
"""),_display_(/*3.2*/defining(play.core.PlayVersion.current)/*3.41*/ { version =>_display_(Seq[Any](format.raw/*3.54*/("""

    """),format.raw/*5.5*/("""<section id="top">
        <div class="wrapper">
            <h1><a href="https://playframework.com/documentation/"""),_display_(/*7.67*/version),format.raw/*7.74*/("""/Home">"""),_display_(/*7.82*/message),format.raw/*7.89*/("""</a></h1>
        </div>
    </section>

    <div id="content" class="wrapper doc">
        <article>

            <h1>Welcome to Play</h1>

            <p>
                Congratulations, you’ve just created a new Play application. This page will help you with the next few steps.
            </p>

            <blockquote>
                <p>
                    You’re using Play """),_display_(/*22.40*/version),format.raw/*22.47*/("""
                """),format.raw/*23.17*/("""</p>
            </blockquote>

            <h2>Why do you see this page?</h2>

            <p>
                The <code>conf/routes</code> file defines a route that tells Play to invoke the <code>HomeController.index</code> action
                whenever a browser requests the <code>/</code> URI using the GET method:
            </p>

            <pre><code># Home page
GET     /               controllers.ApplicationController.index</code></pre>

            <p>
                Play has invoked the <code>controllers.ApplicationController.index</code> method to obtain the <code>Action</code> to execute:
            </p>

            <pre><code>def index = Action """),format.raw/*40.43*/("""{"""),format.raw/*40.44*/(""" """),format.raw/*40.45*/("""implicit request: Request[AnyContent] =>
    Ok(views.html.index("Your new application is ready!"))
"""),format.raw/*42.1*/("""}"""),format.raw/*42.2*/("""</code></pre>

            <p>
                An action is a function that handles the incoming HTTP request, and returns the HTTP result to send back to the web client.
                Here we send a <code>200 OK</code> response, using a template to fill its content.
            </p>

            <p>
                The template is defined in the <code>app/views/index.scala.html</code> file and compiled as a Scala function.
            </p>

            <pre><code>@(message: String)

@main("Welcome to Play") """),format.raw/*55.27*/("""{"""),format.raw/*55.28*/("""

    """),format.raw/*57.5*/("""@welcome(message)

"""),format.raw/*59.1*/("""}"""),format.raw/*59.2*/("""</code></pre>

            <p>
                The first line of the template defines the function signature. Here it just takes a single <code>String</code> parameter.
                This template then calls another function defined in <code>app/views/main.scala.html</code>, which displays the HTML
                layout, and another function that displays this welcome message. You can freely add any HTML fragment mixed with Scala
                code in this file.
            </p>

            <p>You can read more about <a href="https://www.playframework.com/documentation/"""),_display_(/*68.94*/version),format.raw/*68.101*/("""/ScalaTemplates">Twirl</a>, the template language used by Play, and how Play handles <a href="https://www.playframework.com/documentation/"""),_display_(/*68.240*/version),format.raw/*68.247*/("""/ScalaActions">actions</a>.</p>

            <h2>Need more info on the console?</h2>

            <p>
                For more information on the various commands you can run on Play, i.e. running tests and packaging applications for production, see <a href="https://playframework.com/documentation/"""),_display_(/*73.199*/version),format.raw/*73.206*/("""/PlayConsole">Using the Play console</a>.
            </p>

            <h2>Need to set up an IDE?</h2>

            <p>
                You can start hacking your application right now using any text editor. Any changes will be automatically reloaded at each page refresh,
                including modifications made to Scala source files.
            </p>

            <p>
                If you want to set-up your application in <strong>IntelliJ IDEA</strong> or any other Java IDE, check the
                <a href="https://www.playframework.com/documentation/"""),_display_(/*85.71*/version),format.raw/*85.78*/("""/IDE">Setting up your preferred IDE</a> page.
            </p>

            <h2>Need more documentation?</h2>

            <p>
                Play documentation is available at <a href="https://www.playframework.com/documentation/"""),_display_(/*91.106*/version),format.raw/*91.113*/("""">https://www.playframework.com/documentation</a>.
            </p>

            <p>
                Play comes with lots of example templates showcasing various bits of Play functionality at <a href="https://www.playframework.com/download#examples">https://www.playframework.com/download#examples</a>.
            </p>

            <h2>Need more help?</h2>

            <p>
                Play questions are asked and answered on Stackoverflow using the "playframework" tag: <a href="https://stackoverflow.com/questions/tagged/playframework">https://stackoverflow.com/questions/tagged/playframework</a>
            </p>

            <p>
                The <a href="http://groups.google.com/group/play-framework">Play Google Group</a> is where Play users come to seek help,
                announce projects, and discuss issues and new features. If you don’t have a Google account, you can still join the mailing
                list by sending an e-mail to
                <strong>play-framework+subscribe@googlegroups.com</strong>.
            </p>

            <p>
                Gitter is a real time chat channel, like IRC. The <a href="https://gitter.im/playframework/playframework">playframework/playframework</a>  channel is used by Play users to discuss the ins and outs of writing great Play applications.
            </p>

        </article>

        <aside>
            <h3>Browse</h3>
            <ul>
                <li><a href="https://playframework.com/documentation/"""),_display_(/*120.71*/version),format.raw/*120.78*/("""">Documentation</a></li>
                <li><a href="https://playframework.com/documentation/"""),_display_(/*121.71*/version),format.raw/*121.78*/("""/api/"""),_display_(/*121.84*/style),format.raw/*121.89*/("""/index.html">Browse the """),_display_(/*121.114*/{style.capitalize}),format.raw/*121.132*/(""" """),format.raw/*121.133*/("""API</a></li>
            </ul>
            <h3>Start here</h3>
            <ul>
                <li><a href="https://playframework.com/documentation/"""),_display_(/*125.71*/version),format.raw/*125.78*/("""/PlayConsole">Using the Play console</a></li>
                <li><a href="https://playframework.com/documentation/"""),_display_(/*126.71*/version),format.raw/*126.78*/("""/IDE">Setting up your preferred IDE</a></li>
                <li><a href="https://playframework.com/download#examples">Example Projects</a>
            </ul>
            <h3>Help here</h3>
            <ul>
                <li><a href="https://stackoverflow.com/questions/tagged/playframework">Stack Overflow</a></li>
                <li><a href="http://groups.google.com/group/play-framework">Mailing List</a></li>
                <li><a href="https://gitter.im/playframework/playframework">Gitter Channel</a></li>
            </ul>

        </aside>

    </div>
""")))}))
      }
    }
  }

  def render(message:String,style:String): play.twirl.api.HtmlFormat.Appendable = apply(message,style)

  def f:((String,String) => play.twirl.api.HtmlFormat.Appendable) = (message,style) => apply(message,style)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Wed Apr 17 10:37:52 EDT 2019
                  SOURCE: /Data/2019/git/AHRM/store/app/views/welcome.scala.html
                  HASH: 7ba0b6c0c06c6e6d07b5e43ed81f1e1f6622e3e9
                  MATRIX: 738->1|874->44|901->46|948->85|998->98|1030->104|1171->219|1198->226|1232->234|1259->241|1671->626|1699->633|1744->650|2444->1322|2473->1323|2502->1324|2629->1424|2657->1425|3201->1943|3230->1944|3263->1950|3309->1970|3337->1971|3947->2554|3976->2561|4143->2700|4172->2707|4500->3007|4529->3014|5124->3582|5152->3589|5412->3821|5441->3828|6957->5317|6986->5324|7109->5419|7138->5426|7172->5432|7199->5437|7253->5462|7294->5480|7325->5481|7503->5631|7532->5638|7676->5754|7705->5761
                  LINES: 21->1|26->2|27->3|27->3|27->3|29->5|31->7|31->7|31->7|31->7|46->22|46->22|47->23|64->40|64->40|64->40|66->42|66->42|79->55|79->55|81->57|83->59|83->59|92->68|92->68|92->68|92->68|97->73|97->73|109->85|109->85|115->91|115->91|144->120|144->120|145->121|145->121|145->121|145->121|145->121|145->121|145->121|149->125|149->125|150->126|150->126
                  -- GENERATED --
              */
          