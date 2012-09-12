package database

import play.api.db._
import play.api.Play.current
import org.scalaquery.ql._
import org.scalaquery.ql.extended.ExtendedTable
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session._
import org.scalaquery.ql.basic.BasicDriver.Implicit._

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
  def entry(_name: String) = db.withSession { implicit db: Session =>
  	Users.insert(_name)
  }

  // 登録済か確認
  def isEntry = db.withSession { implicit db: Session =>
  }


}
