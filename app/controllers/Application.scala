package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class Application @Inject() (ws: WSClient) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async {
    import org.joda.time._
    import org.joda.time.format.DateTimeFormat
    import models.SunInfo

    val responseF = ws.url("http://api.sunrise-sunset.org/json?" +
        "lat=-33.8830&lng=151.2167&formatted=0").get()

    val weatherResponseF = ws.url("http://api.openweathermap.org/data/2.5/" +
        "weather?lat=-33.8830&lon=151.2167&units=metric").get()

    for {
      response <- responseF
      weatherResponse <- weatherResponseF
    } yield {
      val weatherJson = weatherResponseF.json
      val temperature = (json \ "main" \ "temp").as[Double]

      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunInfo = SunInfo(sunriseTimeStr, sunsetTimeStr)

      Ok(views.html.index(sunInfo, temperature))
    }
  }

}
