package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.db._
import play.api.mvc.Security._
import play.api.cache.Cache
import play.api.Play.current

object LoginController extends Controller {
  
  // ログインフォーム
  val loginForm: Form[models.Login] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(models.Login.apply)(models.Login.unapply)
  )
  
  def login = Action {
    Ok(views.html.login(loginForm))
  }

  //def logincheck = TODO

  def logincheck = Action { implicit request =>
    /*
      値を受け取り、DBと比較
    */

    //val user: models.Login = loginForm.bindFromRequest.get

    loginForm.bindFromRequest.fold(
      // error
      errors => BadRequest(views.html.login(errors)),
      // success
      user => {
        Redirect(routes.PersonalPageController.personal(user.username)).withSession(
        // セッションに値を格納
          "username" -> user.username
        )
      }
    )
    // play.api.mvc.Security.Authenticated 

  } 

  def logoff = Action { implicit request =>
    // もっと正しいやり方があるはず
    val username: String = "" 
    Cache.set("username", "")

    // セッションをまっさらに
    Ok(views.html.index(username)).withNewSession
  }
  
  def callback = Action { implicit request =>
    Ok(views.html.index(""))
  }
}
