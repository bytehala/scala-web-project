package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class Application extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    import java.util.Date
    import java.text.SimpleDateFormat

    val date = new Date()
    val dateStr = new SimpleDateFormat().format(date)

    Ok(views.html.index(dateStr))
  }

}
