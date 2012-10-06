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

object User_Live extends ExtendedTable[(Int, Int, String)]("USER_LIVE") {
  def userID = column[Int]("userID", O NotNull)
  def liveID = column[Int]("liveID", O NotNull)
  def comment = column[String]("comment")
  def * = userID ~ liveID ~ comment

  lazy val db = Database.forDataSource(DB.getDataSource())

  // テーブルへ登録 
  // userID と liveID の紐付け
  def create(_userID: Int, _liveID: Int, _comment: String) = db.withSession { implicit session: Session =>
    try {
      User_Live.insert(_userID, _liveID, _comment)
      true
    } catch {
      case e: PSQLException => false
    }
  }

  /*
   * 一覧作成
   * (Int) -> (Vector[(String, String, java.sql.Date, String)])
   * */
  def read(_userID: Int) = db.withSession { implicit session: Session =>
    // userIDからライブIDを引っ張る
    val queryLiveID = User_Live.where(_.userID === _userID) map {
      id => id.liveID ~ id.comment
    }
    // マッチしたライブID一覧取得
    val liveIDList = queryLiveID.list()

    // ライブIDをもとに、LiveListsからライブ情報を引っ張る
    val ite = liveIDList.iterator

    //var vec = Vector.empty[(String, String, java.sql.Date, String, String)]
    // var vec = Vector.newBuilder[models.LiveList]
    var vec = Vector.newBuilder[models.DataOfLiveLists]

    ite.foreach(list => {
      val query = LiveLists.where(_.liveID === list._1) map {
        live => live.live_name ~ live.artist ~ live.date ~ live.place ~ live.liveID
      }
      val info = query.first
      
      vec += new models.DataOfLiveLists(info._3, info._1, info._2, info._4, list._2, _userID,  info._5)
    })
    vec.result
  }

}