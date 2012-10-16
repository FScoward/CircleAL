package controllers

import play.api._
import play.api.mvc._
import play.api.libs.oauth._
import play.api.libs.ws._

object TwitterController extends Controller {
  // ConsumerKey(key, secret)
  val KEY = ConsumerKey("yU1ZaY1xoEHHmRkQVkzhqQ", "U1KTA1oIibflyZ34qqNyvtQD1CWdfMWwuovupnMCtQQ")

  // ServiceInfo(requestTokenURL, accessTokenURL, authorizationURL, key)
  val TWITTER = OAuth(ServiceInfo(
    "https://api.twitter.com/oauth/request_token",
    "https://api.twitter.com/oauth/access_token",
    "https://api.twitter.com/oauth/authorize", KEY),
  false)

  def authenticate = Action { request =>
    request.queryString.get("oauth_verifier").flatMap(_.headOption).map { verifier =>
      val tokenPair = sessionTokenPair(request).get
      // We got the verifier; now get the access token, store it and back to index
      TWITTER.retrieveAccessToken(tokenPair, verifier) match {
        case Right(t) => {
          // We received the authorized tokens in the OAuth object - store it before we proceed
          Redirect(routes.Application.index).withSession("token" -> t.token, "secret" -> t.secret)
        }
        case Left(e) => throw e
      }
    }.getOrElse(
      TWITTER.retrieveRequestToken("http://localhost:9000/") match {
        case Right(t) => {
          // We received the unauthorized tokens in the OAuth object - store it before we proceed

          Redirect(TWITTER.redirectUrl(t.token)).withSession("token" -> t.token, "secret" -> t.secret)
        }
        case Left(e) => throw e
      })
  }

  def sessionTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get("token")
      secret <- request.session.get("secret")
    } yield {
      RequestToken(token, secret)
    }
  }

/*
  private def getUserName(token: Option[RequestToken]) = Action { request =>
    Async{
  // TODO useridを取得
    // account/verify_credentials
    // "http://api.twitter.com/1/account/verify_credentials.format"
      val tokenPair = token.get
      val rt = RequestToken(tokenPair.token, tokenPair.secret)
    
      WS.url("http://api.twitter.com/1/account/verify_credentials.json").sign(OAuthCalculator(KEY, rt)).get
    
    }
  }
  */
}
