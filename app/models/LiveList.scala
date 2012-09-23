package models
import java.util.Date
import org.scalaquery.ql._
import org.scalaquery.ql.extended.ExtendedTable
import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.session.Database
import org.scalaquery.session.Database.threadLocalSession
import java.text.DateFormat

// フォーム定義
case class LiveList(_date: Date, _live_name: String, _artist: String, _place: String, _comment: String)
