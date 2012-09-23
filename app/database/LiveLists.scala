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

	// 登録
	/*
		日付とライブ名でダブリチェック
	*/
	def entry(_live_name: String, _artist: String, _date: java.sql.Date, _place: String) = db.withSession { implicit session: Session =>
	  
		var flag = false
		try{
			live_name ~ artist ~ date ~ place insert(_live_name, _artist, _date, _place)
			flag = true
		}catch{
			case e: PSQLException => flag = false
		}
		flag
	}

	def search(_live_name: String, _artist: String, _date: java.sql.Date, _place: String) = db.withSession { implicit session: Session =>
		// ライブID取得
		val sel = for(
				list <- LiveLists 
				if list.live_name is _live_name
				if list.artist is _artist
				if list.date is _date
				if list.place is _place
			){}


	}
}