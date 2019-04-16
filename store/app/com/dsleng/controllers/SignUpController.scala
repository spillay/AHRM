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
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{JsError, Json}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, ControllerComponents}
import com.dsleng.service.UserService
import com.dsleng.utils.auth.DefaultEnv
import com.dsleng.utils.responses.rest.Bad
import com.mohiva.play.silhouette.api.Silhouette
import scala.concurrent.{ExecutionContext, Future}

@Api(value = "Jaasuz Authentication")
class SignUpController @Inject()(components: ControllerComponents,
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

  @ApiOperation(value = "", hidden = true)
  def register = silhouette.SecuredAction { implicit request =>
    println("in register")
    Ok(Json.toJson(request.identity))
    //Ok(views.html.index(request.identity.username))
  }

  @ApiOperation(value = "Register and get authentication token", response = classOf[Token])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        value = "SignUp",
        required = true,
        dataType = "com.dsleng.models.security.SignUp",
        paramType = "body"
      )
    )
  )
  def signUpUser = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    println("In Signup")
    println(s"Request from ${request.remoteAddress}: ${request} ${request.headers}")
    request.body.validate[SignUp].map { signUp =>
     println("------------------------saving123-------------------")
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
      userService.retrieve(loginInfo).flatMap {
        case None => // user not already exists 
          val user = User(None, loginInfo, loginInfo.providerKey, signUp.email, signUp.firstName, signUp.lastName, None, true)
          // val plainPassword = UUID.randomUUID().toString.replaceAll("-", "")
          val authInfo = passwordHasherRegistry.current.hash(signUp.password)
          for {
            avatar <- avatarService.retrieveURL(signUp.email)
            userToSave <- userService.save(user.copy(avatarURL = avatar))
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(loginInfo)
            token <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(token,
              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDateTime)))
            )
          } yield {
            println("------------------------after yield-------------------")
            val url = routes.ApplicationController.index().absoluteURL()
            mailerClient.send(Email(
              subject = Messages("email.sign.up.subject"),
              from = Messages("email.from"),
              to = Seq(user.email),
              bodyText = Some(views.txt.emails.signUp(user, url).body),
              bodyHtml = Some(views.html.emails.signUp(user, url).body)
            ))
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            result
          }
        case Some(_) => // user already exists!
          Future(Conflict(Json.toJson(Bad(message = "user already exists"))))
      }
    }
    .recoverTotal {
      case error =>
        Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toJson(error)))))
    }
  }
}
