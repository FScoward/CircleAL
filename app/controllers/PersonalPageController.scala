package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.iteratee._

object PersonalPageController extends Controller {

  // ライブ情報
  val liveInfoForm: Form[models.LiveList] = Form(
	mapping(
		"date" -> date,
		"live_name" -> text
	)(models.LiveList.apply)(models.LiveList.unapply)
  )

  // アニメリスト 
  val animeListForm: Form[models.AnimeList] = Form(
    mapping(
      "title" -> nonEmptyText
    )(models.AnimeList.apply)(models.AnimeList.unapply)
  )

  def personal(implicit username: String) = Action { implicit request =>
    val oauth_verifier = request.queryString.get("oauth_verifier")

    if (username.nonEmpty) {
      Ok(views.html.personal(animeListForm, liveInfoForm, username))
    }else{
      Unauthorized("Error")
    }

/*
  session.get("username").map { user =>
      Ok(views.html.personal(animeListForm, Some(user)))
    }.getOrElse {
      Unauthorized("Error >> 不正なアクセスです。")
    }

    */
  }

  def test = WebSocket.using[String] { request =>
    val out = Enumerator.imperative[String]()

    val in = Iteratee.foreach[String] { message =>
      val say = message.split("<>")
      
      val send = "<blockquote>" + say(0) + "<p>" + say(1) + "</p></blockquotes>"
      
      out.push(send)
    }

    (in, out)
  }
  
}

