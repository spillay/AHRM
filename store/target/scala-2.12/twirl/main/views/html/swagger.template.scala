
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
/*1.2*/import play.api.Play.current

object swagger extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""<!-- HTML for static distribution bundle build -->
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>Swagger UI</title>
    <link rel="stylesheet" type="text/css" href="/assets/swagger-ui/swagger-ui.css" >
    <link rel="icon" type="image/png" href="./favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="./favicon-16x16.png" sizes="16x16" />
    <style>
      html
      """),format.raw/*13.7*/("""{"""),format.raw/*13.8*/("""
        """),format.raw/*14.9*/("""box-sizing: border-box;
        overflow: -moz-scrollbars-vertical;
        overflow-y: scroll;
      """),format.raw/*17.7*/("""}"""),format.raw/*17.8*/("""

      """),format.raw/*19.7*/("""*,
      *:before,
      *:after
      """),format.raw/*22.7*/("""{"""),format.raw/*22.8*/("""
        """),format.raw/*23.9*/("""box-sizing: inherit;
      """),format.raw/*24.7*/("""}"""),format.raw/*24.8*/("""

      """),format.raw/*26.7*/("""body
      """),format.raw/*27.7*/("""{"""),format.raw/*27.8*/("""
        """),format.raw/*28.9*/("""margin:0;
        background: #fafafa;
      """),format.raw/*30.7*/("""}"""),format.raw/*30.8*/("""
    """),format.raw/*31.5*/("""</style>
  </head>

  <body>
    <div id="swagger-ui"></div>

    <script src="/assets/swagger-ui/swagger-ui-bundle.js"> </script>
    <script src="/assets/swagger-ui/swagger-ui-standalone-preset.js"> </script>
    <script>
    window.onload = function() """),format.raw/*40.32*/("""{"""),format.raw/*40.33*/("""
      """),format.raw/*41.7*/("""var url = window.location.search.match(/url=([^&]+)/);
      if (url && url.length > 1) """),format.raw/*42.34*/("""{"""),format.raw/*42.35*/("""
        """),format.raw/*43.9*/("""url = url[1];
      """),format.raw/*44.7*/("""}"""),format.raw/*44.8*/(""" """),format.raw/*44.9*/("""else """),format.raw/*44.14*/("""{"""),format.raw/*44.15*/("""
        """),format.raw/*45.9*/("""url = """"),_display_(/*45.17*/{s"${current.configuration.getString("swagger.api.basepath")
            .getOrElse("http://localhost:9000")}/swagger.json"}),format.raw/*46.64*/(""""
      """),format.raw/*47.7*/("""}"""),format.raw/*47.8*/("""
      """),format.raw/*48.7*/("""// Begin Swagger UI call region
      const ui = SwaggerUIBundle("""),format.raw/*49.34*/("""{"""),format.raw/*49.35*/("""
        """),format.raw/*50.9*/("""url: url,
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          SwaggerUIStandalonePreset
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout",
        onComplete: function() """),format.raw/*61.32*/("""{"""),format.raw/*61.33*/("""
          """),format.raw/*62.11*/("""// "basicAuth" is the key name of the security scheme in securityDefinitions
          ui.preauthorizeBasic("basicAuth", "username", "password");
        """),format.raw/*64.9*/("""}"""),format.raw/*64.10*/("""
      """),format.raw/*65.7*/("""}"""),format.raw/*65.8*/(""")
      // End Swagger UI call region

      window.ui = ui
    """),format.raw/*69.5*/("""}"""),format.raw/*69.6*/("""
  """),format.raw/*70.3*/("""</script>
  </body>
  Hello All
</html>
"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Wed Apr 17 10:37:52 EDT 2019
                  SOURCE: /Data/2019/git/AHRM/store/app/views/swagger.scala.html
                  HASH: e79e058f9ec96e0545480ca05fe083c36aa9f4d8
                  MATRIX: 432->1|849->30|1305->459|1333->460|1369->469|1498->571|1526->572|1561->580|1627->619|1655->620|1691->629|1745->656|1773->657|1808->665|1846->676|1874->677|1910->686|1982->731|2010->732|2042->737|2325->992|2354->993|2388->1000|2504->1088|2533->1089|2569->1098|2616->1118|2644->1119|2672->1120|2705->1125|2734->1126|2770->1135|2805->1143|2950->1267|2985->1275|3013->1276|3047->1283|3140->1348|3169->1349|3205->1358|3550->1675|3579->1676|3618->1687|3799->1841|3828->1842|3862->1849|3890->1850|3981->1914|4009->1915|4039->1918
                  LINES: 17->1|27->2|38->13|38->13|39->14|42->17|42->17|44->19|47->22|47->22|48->23|49->24|49->24|51->26|52->27|52->27|53->28|55->30|55->30|56->31|65->40|65->40|66->41|67->42|67->42|68->43|69->44|69->44|69->44|69->44|69->44|70->45|70->45|71->46|72->47|72->47|73->48|74->49|74->49|75->50|86->61|86->61|87->62|89->64|89->64|90->65|90->65|94->69|94->69|95->70
                  -- GENERATED --
              */
          