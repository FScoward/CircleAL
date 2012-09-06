package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import twitter4j._
import twitter4j.auth._

object Application extends Controller {
  
  def index = Action { implicit request =>

    /*
    var oauth_verifier = ""
    if (request.queryString.get("oauth_verifier").nonEmpty){
      oauth_verifier = request.queryString.get("oauth_verifier").get.mkString
      // ユーザ名の取得
      getTwitUser(oauth_verifier)
    }
    */
    
    Twitter4jController.getTwitUser
    

    //val params: Map[String, Seq[String]] = request.queryString
    //val oauth_verifier = params("oauth_verifier").head

    //val username = session.get("username").getOrElse("")
    //val username = Cache.get("username").get.asInstanceOf[String]
    val username = Cache.get("username").getOrElse("").asInstanceOf[String]
    Ok(views.html.index(username))
  }

/*
  def personal = Action { implicit request =>
    Ok(views.html.personal())
  }
  */
  // Twitterのユーザ情報取得
  def getTwitUser = {
    
    if( Cache.get("Twitter") != None){
      val twitter = Cache.get("Twitter").get.asInstanceOf[Twitter]
      var accessToken: AccessToken = null
      
      try{
        val requestToken = Cache.get("RequestToken").get.asInstanceOf[RequestToken]
        //accessToken = twitter.getOAuthAccessToken(requestToken, _oauth_verifier)
        accessToken = twitter.getOAuthAccessToken(requestToken)
      }catch{
        case _ => Cache.set("username", "")
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
}
