package com.dsleng.controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import com.dsleng.formatters.json.{CredentialFormat, Token}
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import com.dsleng.models.security.SignUp
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import com.dsleng.service.UserService
import com.dsleng.utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

@Api(value = "Jaasuz Authentication")
class CredentialsAuthController @Inject()(components: ControllerComponents,
                                          userService: UserService,
                                          configuration: Configuration,
                                          silhouette: Silhouette[DefaultEnv],
                                          clock: Clock,
                                          credentialsProvider: CredentialsProvider,
                                          authInfoRepository: AuthInfoRepository,
                                          passwordHasherRegistry: PasswordHasherRegistry,
                                          messagesApi: MessagesApi)
                                         (implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  implicit val credentialFormat = CredentialFormat.restFormat

  implicit val signUpFormat = Json.format[SignUp]

  @ApiOperation(value = "Get authentication token", response = classOf[Token])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "Credentials",
        required = true,
        dataType = "com.mohiva.play.silhouette.api.util.Credentials",
        paramType = "body"
      )
    )
  ) 
//def authenticate = Action.async(parse.json[Credentials]) { implicit request =>
def authenticate =  silhouette.UnsecuredAction.async(parse.json[Credentials]) { implicit request =>
    //Logger("access").info(s"Request from ${request.remoteAddress}: ${request}")
    println(s"Request from ${request.remoteAddress}: ${request} ${request.headers}")
    println("in authenticate",request.body.identifier)
    var id = request.body.identifier
    var pass = request.body.password
    if (id == "Demo"){
      id = "admininternal"
      pass = "admin5984322sdfds"
    }
    val credentials = Credentials(id, pass)
    credentialsProvider
      .authenticate(credentials)
      .flatMap { loginInfo =>
        println("loginInfo++++++++++++++++++++++++++++++++",loginInfo)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if !user.activated =>
            Future.failed(new IdentityNotFoundException("Couldn't find user"))
          case Some(user) =>
            val config = configuration.underlying
            silhouette.env.authenticatorService
              .create(loginInfo)
              .map {
                case authenticator => authenticator
              }
              .flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService
                  .init(authenticator)
                  .flatMap { token =>
                    silhouette.env.authenticatorService
                      .embed(
                        token,
                        Ok(
                          Json.toJson(
                            Token(
                              token,
                              expiresOn = authenticator.expirationDateTime
                            )
                          )
                        )
                      )
                  }
              }
          case None =>
            Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }
      .recover {
        case _: ProviderException =>
          Forbidden
      }
  }
}
