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

	// 作成
	def create(_userID: Int, _liveID: Int, _comment: String) = db.withSession { implicit session: Session =>
		// userID と liveID の紐付け
		User_Live.insert(_userID, _liveID, _comment)
	}
}