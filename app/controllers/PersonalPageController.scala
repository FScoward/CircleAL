package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.iteratee._
import play.api.cache.Cache
import play.api.Play.current

object PersonalPageController extends Controller {

  // ライブ情報
  val liveInfoForm: Form[models.LiveList] = Form(
	mapping(
		"date" -> date,
		"artist" -> text,
		"place" -> text,
		"comment" -> text
	)(models.LiveList.apply)(models.LiveList.unapply)
  )

  // アニメリスト
  val animeListForm: Form[models.AnimeList] = Form(
    mapping(
      "title" -> nonEmptyText
    )(models.AnimeList.apply)(models.AnimeList.unapply)
  )

  def personal(implicit username: String) = Action { implicit request =>
    // val oauth_verifier = request.queryString.get("oauth_verifier")

    val username = Cache.get("username").getOrElse("").asInstanceOf[String]
    
    if (username.nonEmpty) {
      Ok(views.html.personal(animeListForm, liveInfoForm, username))
    }else{
      Unauthorized("Error")
    }


  }

  def test = WebSocket.using[String] { request =>
    val out = Enumerator.imperative[String]()

    val in = Iteratee.foreach[String] { message =>
      val say = message.split("<>")
      

      val length = say.length
      
      if(length >= 3){
	      // 日付チェック
        
	      var send = "<blockquote style='background-color: ivory;'>"
	        
	      send = send +
	      say(0) +
	      "<p><span class='badge badge-important'>アーティスト</span> " + 
	      say(1) +
	      "<span class='badge badge-warning'>会場</span> " + 
	      say(2)
	      
	      if(length == 4){
	        send = send + "<small><span class='badge badge-success'>コメント</span> " + say(3) + "</small>"
	      }
	      send = send + "</p></blockquotes>"
	      out.push(send)
      }else{
        var send = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>失敗</div>"
        out.push(send)
      }
    }

    (in, out)
  }

}

