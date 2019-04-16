package com.dsleng.controllers

import javax.inject.{ Inject, Singleton }

import com.dsleng.formatters.json.{CredentialFormat, Token}
import com.dsleng.utils.responses.rest.Bad
import com.dsleng.utils.responses.rest.SPResponseListener
import com.mohiva.play.silhouette.api.Silhouette
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import play.api.libs.json.Json
import play.api.mvc._
import com.dsleng.utils.auth.DefaultEnv
import com.dsleng.models.design.EmotionColors

import scala.concurrent.Future


@Api(value = "Jaasuz Utils")
@Singleton
class ApplicationController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]) extends AbstractController(components) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  @ApiOperation(value = "", hidden = true)
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  @ApiOperation(value = "", hidden = true)
  def swagger = Action {
    request =>
      Ok(views.html.swagger())
  }
  
  @ApiOperation(value = "", hidden = true)
  def redirectDocs = Action { implicit request =>
    println("redirectDocs")
    println(request.host)
    Redirect(
      url = "/assets/lib/swagger-ui/index.html",
      queryString = Map("url" -> Seq("http://" + request.host + "/swagger.json")))
    /*
    Redirect(
      url = "/assets/lib/swagger-ui/index.html",
      queryString = Map("url" -> Seq("http://" + request.host + "/swagger.json")))
    */  
  }

  @ApiOperation(value = "Get bad password value",hidden=true)
  def badPassword = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.obj("result" -> "qwerty1234")))
  }

  @ApiOperation(value = "Get colors", response = classOf[EmotionColors])
  def colors = Action.async {
    //Future.successful(Ok(Json.arr("black", "blue", "green", "red", "white")))
    val emo = new EmotionColors(Array());
    Future.successful(Ok(Json.toJson(emo.colors)))
  }

  
 /**
   * Gets a user.
   */
  @ApiOperation(value = "", hidden = true)
  def user = silhouette.SecuredAction { implicit request =>
    Ok(Json.toJson(request.identity))
  }

  /**
   * Checks if a user is authenticated.
   */
  @ApiOperation(value = "", hidden = true)
  def isAuthenticated = silhouette.UserAwareAction { implicit request =>
    println("in isAuthenticated")
    println(s"    server content-type: ${request.contentType}")
            println(s"    server headers: ${request.headers}")
            println(s"    server body: ${request.body}")
            println(s"    server query string: ${request.rawQueryString}")
    request.identity match {
      case Some(identity) => Ok
      case None => Unauthorized
    }
  }
 
}
