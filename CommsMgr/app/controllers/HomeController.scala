package controllers

import play.api.libs.concurrent.CustomExecutionContext
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import scala.concurrent.Future
import javax.inject._
import play.api._
import play.api.mvc._
import com.dsleng.email.EmailAnalysis
import com.dsleng.email.utils.FileStore

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val executor =  scala.concurrent.ExecutionContext.global
//  def index() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.index())
//  }
  def process() = Action.async { 
    Future {
      val emailStore = "/Users/suresh/enron/junk/"
      //val fs = new FileStore(emailStore,false)
      //fs.process()
      //val ws = new EmailAnalysis()
      //val v = ws.processGender("We miss you")
      //println("webservice ('male', '59.10%')" + v)
      Ok("done")
    }(executor)
    
  }
}

/*
trait MyExecutionContext extends ExecutionContext

class MyExecutionContextImpl @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "my.executor") with MyExecutionContext

class HomeController @Inject()(myExecutionContext: MyExecutionContext, val controllerComponents: ControllerComponents) extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    
    Ok(views.html.index())
  }
  def process = Action.async {
    Future {
      val ws = new EmailAnalysis()
      val v = ws.processGender("We miss you")
      println("webservice " + v)
      Ok(v)
    }(myExecutionContext)
  }
}
* 
*/
