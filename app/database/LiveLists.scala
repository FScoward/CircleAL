package database

import play.api.db._
import play.api.Play.current
import org.scalaquery.ql._
import org.scalaquery.ql.extended.ExtendedTable
import org.scalaquery.ql.extended.PostgresDriver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session._
//import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.postgresql.util.PSQLException
import play.api.cache.Cache

object LiveLists extends ExtendedTable[(Int, String, String, java.sql.Date, String)]("Live") {
  def liveID = column[Int]("liveID", O AutoInc)
  def live_name = column[String]("live_name", O NotNull)
  def artist = column[String]("artist", O NotNull)
  def date = column[java.sql.Date]("date", O NotNull)
  def place = column[String]("place", O NotNull)
  def * = liveID ~ live_name ~ artist ~ date ~ place

  // 外部キー
  //def live = foreignKey("live_fk", liveID, LiveLists)

  lazy val db = Database.forDataSource(DB.getDataSource())

  /*
   * ライブ情報の登録
   * (String, String, Date, String, String) -> (Boolean)
   */
  def entry(_live_name: String, _artist: String, _date: java.sql.Date, _place: String, _comment: String) = db.withSession { implicit session: Session =>
    var flag = false

    try {
      if (searchLiveID(_live_name, _artist, _date, _place) > 0) {

      } else {
        // 登録
        live_name ~ artist ~ date ~ place insert (_live_name, _artist, _date, _place)
      }

      flag = true
    } catch {
      case e: PSQLException => flag = false
    }
    flag
  }

  /*
	ユーザー情報とライブ情報の紐付け
	(String, String, Date, String, String) -> Unit 
	*/
  def correlate(_live_name: String, _artist: String, _date: java.sql.Date, _place: String, _comment: String) = db.withSession { implicit session: Session =>
    // ユーザ名を受け取ってユーザID取得
    val userName = Cache.get("username").getOrElse("").asInstanceOf[String]
    val userID = Users.searchUserID(userName)
    // ライブID取得
    val liveID = searchLiveID(_live_name, _artist, _date, _place)
    // ユーザIDとライブIDの紐付け
    User_Live.create(userID, liveID, _comment)
  }

  /*
	 ライブID検索
	 (String, String, Date, String) -> (Int)
	 */
  def searchLiveID(_live_name: String, _artist: String, _date: java.sql.Date, _place: String) = db.withSession { implicit session: Session =>
    // ライブID取得
    val sel =
      for (
        list <- LiveLists if list.live_name is _live_name if list.artist is _artist if list.date is _date if list.place is _place
      ) yield list.liveID

      try{
	    sel.first()
      }catch{
        case e: NoSuchElementException => -1
      }
  }

  /*
   * ソートして20件取得
   * (Int) -> (List[(Int, String, String, java.sql.Date, String)] 
   */
  def read(_start: Int): List[(Int, String, String, java.sql.Date, String)] = db.withSession { implicit session: Session =>
    // _start件目～_start+20件目まで
    // ex ) xxx.drop(5).take(5)  => 6件目～10件目まで
    val query = Query(LiveLists).orderBy(LiveLists.date.desc).drop(_start - 1).take(20)

    query.list
  }

  def soon = {
    // 現在日付取得
    
    // テーブルから現在日付に近い分を数件取得

    // return

  }
  
  /*
   * USER_LIVEの紐付けを削除
   * */
  def delete(_userID: Int, _liveID: Int) = db.withSession { implicit session: Session =>
    //val query = User_Live.where(ul => (ul.userID ===_userID) && (ul.liveID ===_liveID)).exists
    val query = User_Live.where(ul => (ul.userID ===_userID) && (ul.liveID ===_liveID))
    query.delete
  }
}