package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import services._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class Application (sunService: SunService, weatherService: WeatherService) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async {
    val lat = -33.8830
    val lon = 151.2167

    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
    } yield {
      Ok(views.html.index(sunInfo, temperature))
    }

  }

}
