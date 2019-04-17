// @GENERATOR:play-routes-compiler
// @SOURCE:/Data/2019/git/AHRM/store/conf/routes
// @DATE:Wed Apr 17 10:37:51 EDT 2019

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:20
  FrontendController_1: controllers.FrontendController,
  // @LINE:26
  ApplicationController_0: com.dsleng.controllers.ApplicationController,
  // @LINE:34
  BLController_4: com.dsleng.controllers.BLController,
  // @LINE:47
  CredentialsAuthController_2: com.dsleng.controllers.CredentialsAuthController,
  // @LINE:64
  Assets_3: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:20
    FrontendController_1: controllers.FrontendController,
    // @LINE:26
    ApplicationController_0: com.dsleng.controllers.ApplicationController,
    // @LINE:34
    BLController_4: com.dsleng.controllers.BLController,
    // @LINE:47
    CredentialsAuthController_2: com.dsleng.controllers.CredentialsAuthController,
    // @LINE:64
    Assets_3: controllers.Assets
  ) = this(errorHandler, FrontendController_1, ApplicationController_0, BLController_4, CredentialsAuthController_2, Assets_3, "/")

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, FrontendController_1, ApplicationController_0, BLController_4, CredentialsAuthController_2, Assets_3, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.FrontendController.index()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api""", """com.dsleng.controllers.ApplicationController.index"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/query""", """com.dsleng.controllers.BLController.query"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/emotion""", """com.dsleng.controllers.BLController.emotion"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/entropy""", """com.dsleng.controllers.BLController.entropy"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/deception""", """com.dsleng.controllers.BLController.deception"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/auth/signin/credentials""", """com.dsleng.controllers.CredentialsAuthController.authenticate"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/""" + "$" + """file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:20
  private[this] lazy val controllers_FrontendController_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_FrontendController_index0_invoker = createInvoker(
    FrontendController_1.index(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.FrontendController",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """## NoDocs ###
GET     /swagger.json               controllers.ApiHelpController.getResources
## NoDocs ###""",
      Seq()
    )
  )

  // @LINE:26
  private[this] lazy val com_dsleng_controllers_ApplicationController_index1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api")))
  )
  private[this] lazy val com_dsleng_controllers_ApplicationController_index1_invoker = createInvoker(
    ApplicationController_0.index,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.dsleng.controllers.ApplicationController",
      "index",
      Nil,
      "GET",
      this.prefix + """api""",
      """ API Pages
## NoDocs ###""",
      Seq()
    )
  )

  // @LINE:34
  private[this] lazy val com_dsleng_controllers_BLController_query2_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/query")))
  )
  private[this] lazy val com_dsleng_controllers_BLController_query2_invoker = createInvoker(
    BLController_4.query,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.dsleng.controllers.BLController",
      "query",
      Nil,
      "POST",
      this.prefix + """api/query""",
      """## NoDocs ###""",
      Seq("""nocsrf""")
    )
  )

  // @LINE:37
  private[this] lazy val com_dsleng_controllers_BLController_emotion3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/emotion")))
  )
  private[this] lazy val com_dsleng_controllers_BLController_emotion3_invoker = createInvoker(
    BLController_4.emotion,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.dsleng.controllers.BLController",
      "emotion",
      Nil,
      "POST",
      this.prefix + """api/emotion""",
      """## NoDocs ###""",
      Seq("""nocsrf""")
    )
  )

  // @LINE:40
  private[this] lazy val com_dsleng_controllers_BLController_entropy4_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/entropy")))
  )
  private[this] lazy val com_dsleng_controllers_BLController_entropy4_invoker = createInvoker(
    BLController_4.entropy,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.dsleng.controllers.BLController",
      "entropy",
      Nil,
      "POST",
      this.prefix + """api/entropy""",
      """## NoDocs ###""",
      Seq("""nocsrf""")
    )
  )

  // @LINE:43
  private[this] lazy val com_dsleng_controllers_BLController_deception5_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/deception")))
  )
  private[this] lazy val com_dsleng_controllers_BLController_deception5_invoker = createInvoker(
    BLController_4.deception,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.dsleng.controllers.BLController",
      "deception",
      Nil,
      "POST",
      this.prefix + """api/deception""",
      """## NoDocs ###""",
      Seq("""nocsrf""")
    )
  )

  // @LINE:47
  private[this] lazy val com_dsleng_controllers_CredentialsAuthController_authenticate6_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/auth/signin/credentials")))
  )
  private[this] lazy val com_dsleng_controllers_CredentialsAuthController_authenticate6_invoker = createInvoker(
    CredentialsAuthController_2.authenticate,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.dsleng.controllers.CredentialsAuthController",
      "authenticate",
      Nil,
      "POST",
      this.prefix + """api/auth/signin/credentials""",
      """""",
      Seq()
    )
  )

  // @LINE:64
  private[this] lazy val controllers_Assets_versioned7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned7_invoker = createInvoker(
    Assets_3.versioned(fakeValue[String], fakeValue[Asset]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      this.prefix + """assets/""" + "$" + """file<.+>""",
      """
 # An example route (Prefix all API routes with apiPrefix defined in application.conf)
 GET     /api/summary                controllers.HomeController.appSummary

 # Serve static assets under public directory
GET     /*file                      controllers.FrontendController.assetOrDefault(file)
## NoDocs ###""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:20
    case controllers_FrontendController_index0_route(params@_) =>
      call { 
        controllers_FrontendController_index0_invoker.call(FrontendController_1.index())
      }
  
    // @LINE:26
    case com_dsleng_controllers_ApplicationController_index1_route(params@_) =>
      call { 
        com_dsleng_controllers_ApplicationController_index1_invoker.call(ApplicationController_0.index)
      }
  
    // @LINE:34
    case com_dsleng_controllers_BLController_query2_route(params@_) =>
      call { 
        com_dsleng_controllers_BLController_query2_invoker.call(BLController_4.query)
      }
  
    // @LINE:37
    case com_dsleng_controllers_BLController_emotion3_route(params@_) =>
      call { 
        com_dsleng_controllers_BLController_emotion3_invoker.call(BLController_4.emotion)
      }
  
    // @LINE:40
    case com_dsleng_controllers_BLController_entropy4_route(params@_) =>
      call { 
        com_dsleng_controllers_BLController_entropy4_invoker.call(BLController_4.entropy)
      }
  
    // @LINE:43
    case com_dsleng_controllers_BLController_deception5_route(params@_) =>
      call { 
        com_dsleng_controllers_BLController_deception5_invoker.call(BLController_4.deception)
      }
  
    // @LINE:47
    case com_dsleng_controllers_CredentialsAuthController_authenticate6_route(params@_) =>
      call { 
        com_dsleng_controllers_CredentialsAuthController_authenticate6_invoker.call(CredentialsAuthController_2.authenticate)
      }
  
    // @LINE:64
    case controllers_Assets_versioned7_route(params@_) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned7_invoker.call(Assets_3.versioned(path, file))
      }
  }
}
