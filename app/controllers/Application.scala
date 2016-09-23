package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import services._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws._
import akka.actor.ActorSystem
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import actors.StatsActor
import akka.pattern.ask

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class Application @Inject() (sunService: SunService,
    weatherService: WeatherService,
    actorSystem: ActorSystem
  ) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async {

    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestF = (actorSystem.actorSelection(StatsActor.path) ?
                    StatsActor.GetStats).mapTo[Int]

    val lat = -33.8830
    val lon = 151.2167

    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
      requests <- requestF
    } yield {
      Ok(views.html.index(sunInfo, temperature, requests))
    }

  }

}
