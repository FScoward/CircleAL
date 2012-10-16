package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.cache.Cache
import twitter4j._
import twitter4j.auth._
import play.api.Play.current
import twitter4j.conf.ConfigurationBuilder

object Twitter4jController extends Controller {

  val CONSUMERKEY = "yU1ZaY1xoEHHmRkQVkzhqQ"
  val CONSUMERSECRET= "U1KTA1oIibflyZ34qqNyvtQD1CWdfMWwuovupnMCtQQ"
    
  val confbuilder = new ConfigurationBuilder
  confbuilder.setOAuthConsumerKey(CONSUMERKEY)
  confbuilder.setOAuthConsumerSecret(CONSUMERSECRET)
  val conf = confbuilder.build()
  var twitter: Twitter = null;
  val twitterFactory = new TwitterFactory(conf)
  
  def authenticate = Action { request =>
  	
    twitter = twitterFactory.getInstance

    try{
      // リクエストトークン取得
      val requestToken = twitter.getOAuthRequestToken
      
      Cache.set("RequestToken", requestToken)
      Cache.set("Twitter", twitter)
      
      // 認証ページへ遷移
      Redirect(requestToken.getAuthorizationURL)
      
    }catch{
      case _ => {
        Redirect(routes.Application.index)
      }
    }
  }
  
  def getUserName = {
    // アクセストークン取得
  }
  
    // Twitterのユーザ情報取得
  def getTwitUser = {
    
    if( Cache.get("Twitter") != None){
      val twitter = Cache.get("Twitter").get.asInstanceOf[Twitter]
      var accessToken: AccessToken = null
      
      try{
        val requestToken = Cache.get("RequestToken").get.asInstanceOf[RequestToken]
        accessToken = twitter.getOAuthAccessToken(requestToken)
      }catch{
        case _ => //Cache.set("username", "a")
      }

      if(accessToken != null){
        try{
          val username = twitter.getScreenName
          val profimage = twitter.getProfileImage(username, ProfileImage.MINI)
          Cache.set("username", username)
        }catch{
          case _ => Cache.set("username", "")
        }
      }
      
    }else{
      Cache.set("username", "")
    }
  }

  def logoffTwitter = {}


}
