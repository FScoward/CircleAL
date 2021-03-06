package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.iteratee._
import play.api.cache.Cache
import play.api.Play.current
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException

object PersonalPageController extends Controller {

  // ライブ情報
  val liveInfoForm: Form[models.LiveList] = Form(
    mapping(
      "date" -> date,
      "live_name" -> text,
      "artist" -> text,
      "place" -> text,
      "comment" -> text)(models.LiveList.apply)(models.LiveList.unapply))

  // アニメリスト
  val animeListForm: Form[models.AnimeList] = Form(
    mapping(
      "title" -> nonEmptyText)(models.AnimeList.apply)(models.AnimeList.unapply))

  def personal(implicit username: String) = Action { implicit request =>
    // val oauth_verifier = request.queryString.get("oauth_verifier")

    val username = Cache.get("username").getOrElse("").asInstanceOf[String]

    if (username.nonEmpty) {
      // DB からデータ読み込み
      val userID = database.Users.searchUserID(username)
      val info_vector = database.User_Live.read(userID)
      Ok(views.html.personal(animeListForm, liveInfoForm, username, info_vector))
    } else {
      Unauthorized("Error")
    }

  }

  def test = WebSocket.using[String] { request =>
    val out = Enumerator.imperative[String]()

    val in = Iteratee.foreach[String] { message =>
      val say = message.split("<>")

      var live_name, artist, place, comment = ""
      var date: java.sql.Date = null

      val length = say.length

      if (length >= 4) {
        // 日付チェック

        try{
	      date = java.sql.Date.valueOf(say(0))  
        }catch{
          case e: IllegalArgumentException => date = java.sql.Date.valueOf("1990-01-01")
        }
        
        live_name = say(1)
        artist = say(2)
        place = say(3)

        var send = "<blockquote style='background-color: ivory;'>"

        send = send +
          date +
          "<p><span class='badge badge-info'>ライブ/ツアー名</span> " +
          live_name +
          " <span class='badge badge-important'>アーティスト</span> " +
          artist +
          " <span class='badge badge-warning'>会場</span> " +
          place

        if (length == 5) {
          comment = say(4)
          send = send + "<small><span class='badge badge-success'>コメント</span> " + comment + "</small>"
        }
        send = send + "</p></blockquotes>"

        /*
        DB へ登録
        */
        // 日付変換 String -> java.sql.date
        if (database.LiveLists.entry(live_name, artist, date, place, comment) == false) {
          // send = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>Error: (考えられる理由)既に登録済み</div>"
          database.LiveLists.correlate(live_name, artist, date, place, comment)
        } else {
          database.LiveLists.correlate(live_name, artist, date, place, comment)
        }

        out.push(send)
      } else {
        var send = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>失敗</div>"
        out.push(send)
      }
    }

    (in, out)
  }
  
  def delete = Action { implicit request =>
    // POST から値を取得
    def _userID = request.body.asFormUrlEncoded.get("_userID")(0)
    def _liveID = request.body.asFormUrlEncoded.get("_liveID")(0)
    
    database.LiveLists.delete(_userID.toInt, _liveID.toInt)
    Redirect("/")
  }

}

