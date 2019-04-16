package com.dsleng.controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.dsleng.formatters.json.{CredentialFormat, Token}
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import com.dsleng.models.security.{SignUp, User}
import com.dsleng.models.design.{EmoInput,DecInput,EntropyInput,EmoResponse,DecResponse,EntropyResponse}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{JsError, Json}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.dsleng.service.UserService
import com.dsleng.utils.auth.DefaultEnv
import com.dsleng.utils.responses.rest.Bad
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}
import com.dsleng.service.ESService
import play.api.libs.json._


@Api(value = "Jaasuz API")
class BLController @Inject()(components: ControllerComponents,
                                 userService: UserService,
                                 configuration: Configuration,
                                 silhouette: Silhouette[DefaultEnv],
                                 clock: Clock,
                                 credentialsProvider: CredentialsProvider,
                                 authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry,
                                 avatarService: AvatarService,
                                 mailerClient: MailerClient,
                                 messagesApi: MessagesApi)
                                (implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  implicit val credentialFormat = CredentialFormat.restFormat

  implicit val signUpFormat = Json.format[SignUp]

  @ApiOperation(value = "ES Query", response = classOf[String])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "query",
        required = true,
        dataType = "String",
        paramType = "body"
      )
    )
  )
  def query =  silhouette.UnsecuredAction.async { implicit request =>
  //def query =  silhouette.SecuredAction.async { implicit request =>
  //def query =  Action.async { implicit request =>
      println("before request");
      //Future.successful(Ok(Json.toJson("hello")))
      println(request)
      println(request.body)
      val body = request.body.asJson;
      body.map { content =>
        println(content)
        val nq = Json.stringify(content)
        val index = Json.stringify((content \ "index").get)
        println(index)
        val body = Json.stringify((content \ "body").get)
        val es = new ESService("server");
        val res = es.getSyncData(index,body)
        println(res);
        Future.successful(Ok(Json.toJson(res)))
      }.getOrElse {
        Future.successful(Ok(Json.toJson("Expecting Query")))
      }
      // val nq = (body \ "query").get
      // println(nq)
      //Future.successful(Ok(Json.toJson("hello")))
    //body.map { query =>
    //   // //   println(query)
    //   // //   val es = new ESService("server");
    //   // //   Future.successful(Ok(Json.toJson(es.getSyncData(query))))
    //   // // }.getOrElse {
    //   // //   Future.successful(Ok(Json.toJson("Expecting Query")))
    //   // // } */
  }
  @ApiOperation(value = "Emotion Query", response = classOf[EmoResponse])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "mesg",
        required = true,
        dataType = "com.dsleng.models.design.EmoInput",
        paramType = "body"
      )
    )
  )
  //def emotion =  silhouette.UnsecuredAction.async(parse.json) { implicit request =>
  def emotion =  silhouette.SecuredAction.async(parse.json) { implicit request =>
      Logger("analytics").debug(s"#S#Emotion:Request-Start#S#${request.remoteAddress}#S#${request}#S#${request.body}#S#")
      request.body.validate[EmoInput].map { info =>
        val es = new ESService("server");
        val res = es.getEmotion(info.text)
        Logger("analytics").debug(s"#S#Emotion:Request-End#S#${request.remoteAddress}#S#${request}#S#${request.body}#S#${res}#S#")
        Future.successful(Ok(Json.toJson(res)))
      }.getOrElse {
        Future.successful(Ok(Json.toJson("Expecting Query")))
      }
  }
  // def emotion =  silhouette.UnsecuredAction.async { implicit request =>
  // //def emotion =  silhouette.SecuredAction.async { implicit request =>
  //     println("before request");
  //     println(request)
  //     println(request.body)
  //     val body = request.body.asJson;
  //     body.map { content =>
  //       println(content)
  //       val msg = Json.stringify((content \ "text").get)
  //       val es = new ESService("server");
  //       val res = es.getEmotion(msg)
  //       println("===========================",res)
  //       Future.successful(Ok(Json.toJson(res)))
  //     }.getOrElse {
  //       Future.successful(Ok(Json.toJson("Expecting Query")))
  //     }
  // }
  @ApiOperation(value = "Emotional Entropy", response = classOf[EntropyResponse])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "mesg",
        required = true,
        dataType = "com.dsleng.models.design.EntropyInput",
        paramType = "body"
      )
    )
  )
  //def entropy =  silhouette.UnsecuredAction.async { implicit request =>
  def entropy =  silhouette.SecuredAction.async { implicit request =>
     Logger("analytics").debug(s"#S#Entropy:Request-Start#S#${request.remoteAddress}#S#${request}#S#${request.body}#S#")
      val body = request.body.asJson;
      body.map { content =>
        println(content)
        val msg = Json.stringify((content \ "text").get)
        val es = new ESService("server");
        val res = es.getEntropy(msg)
        Logger("analytics").debug(s"#S#Entropy:Request-End#S#${request.remoteAddress}#S#${request}#S#${request.body}#S#${res}#S#")
        Future.successful(Ok(Json.toJson(res)))
      }.getOrElse {
        Future.successful(Ok(Json.toJson("Expecting Query")))
      }
  }
  // @ApiOperation(value = "Entropy Query", response = classOf[String])
  // @ApiImplicitParams(
  //   Array(
  //     new ApiImplicitParam(
  //       value = "mesg",
  //       required = true,
  //       dataType = "com.dsleng.models.design.Info",
  //       paramType = "body"
  //     )
  //   )
  // )
  // def entropy =  silhouette.UnsecuredAction.async(parse.json) { implicit request =>
  // //def entropy =  silhouette.SecuredAction.async(parse.json) { implicit request =>
      
  //     request.body.validate[Info].map { info =>
  //       val es = new ESService("server");
  //       val res = es.getEntropy(info.text)
  //       println("===========================",info.text,res)
  //       Future.successful(Ok(Json.toJson(res)))
  //     }
  //     Future.successful(Ok(Json.toJson("hello all")))
  //     // .recoverTotal {
  //     //   case error =>
  //     //     Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toJson(error)))))
  //     // }
  //  }
  @ApiOperation(value = "Deception Query", response = classOf[DecResponse])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "mesg",
        required = true,
        dataType = "com.dsleng.models.design.DecInput",
        paramType = "body"
      )
    )
  )
  def deception =  silhouette.SecuredAction.async { implicit request =>
  //def deception =  silhouette.UnsecuredAction.async { implicit request =>
      Logger("analytics").debug(s"#S#Deception:Request-Start#S#${request.remoteAddress}#S#${request}#S#${request.body}#S#")
      val body = request.body.asJson;
      body.map { content =>
        println(content)
        val msg = Json.stringify((content \ "text").get)
        val es = new ESService("server");
        val res = es.getDeception(msg);
        Logger("analytics").debug(s"#S#Deception:Request-End#S#${request.remoteAddress}#S#${request}#S#${request.body}#S#${res}#S#")
        Future.successful(Ok(Json.toJson(res)))
      }.getOrElse {
        Future.successful(Ok(Json.toJson("Expecting Query")))
      }
  }
}
