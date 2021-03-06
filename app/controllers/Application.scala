package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import twitter4j._
import twitter4j.auth._
import database.User_Live

object Application extends Controller {

  def index = Action { implicit request =>
  	
    Twitter4jController.getTwitUser
    
    val username = Cache.get("username").getOrElse("").asInstanceOf[String]

    var url = views.html.index(username)

    if (username != "") {
      /* TODO
       * DB にユーザ名で作成されているか確認
       * 登録がなければテーブルにユーザ作成
       * */
      import database._
      Users.entry(username)

      /*
       * DBからライブ情報の読み込み
       * とりあえずソートして上から20件データ取得
       */
      val userID = Users.searchUserID(username)
      val info_vector = User_Live.read(userID)

      // 遷移するURL
      // url = views.html.personal(PersonalPageController.animeListForm, PersonalPageController.liveInfoForm, username)
      url = views.html.personal(PersonalPageController.animeListForm, PersonalPageController.liveInfoForm, username, info_vector)
    }

    Ok(url)

  }

}
