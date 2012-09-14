package database

import play.api.db._
import play.api.Play.current
import org.scalaquery.ql._
import org.scalaquery.ql.extended.ExtendedTable
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.postgresql.util.PSQLException
// セッションの暗黙的解決
//import org.scalaquery.session.Database.threadLocalSession

/*
	db withSesssion {} 
	db withTransaction {}
	｛｝の中で処理を行う
*/
object Users extends ExtendedTable[(String)]("USER") {
  def name = column[String]("name", O NotNull)
  def * = name

  // Play2で定義した接続情報からScalaQueryのDatabaseを生成
  lazy val db = Database.forDataSource(DB.getDataSource())

  // DBへの登録
  def entry(_name: String) = db.withSession { implicit session: Session =>
  	try{
	  	Users.insert(_name)
  	} catch {
  		case e: PSQLException => "既に登録済みIDです"
  		case _ => "データベースへの登録に失敗しました"
  	}
  }

  // 登録済か確認
  def isEntry(_name: String) = db.withSession { session: Session =>
  	val res = Users.where(_.name is _name)
  	res.exists
  }


}
