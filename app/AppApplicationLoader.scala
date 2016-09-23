import controllers.{Application, Assets}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents
import services.SunService
import services.WeatherService
import scala.concurrent.Future
import akka.actor.Props
import actors.StatsActor
import actors.StatsActor.Ping
import play.api.mvc.Filter
import filters.StatsFilter

class AppApplicationLoader extends ApplicationLoader {

  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      configurator => configurator.configure(context.environment)
    }
    (new BuiltInComponentsFromContext(context) with AppComponents).application
  }
}

trait AppComponents extends BuiltInComponents with AhcWSComponents {
  lazy val assets: Assets = wire[Assets]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]
  lazy val applicationController = wire[Application]

  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)


  val onStart = {
    Logger.info("The app is about to start")
    statsActor ! Ping
  }

  applicationLifecycle.addStopHook {
    () => Logger.info("The app is about to stop")
    Future.successful(Unit)
  }

  lazy val statsActor = actorSystem.actorOf(
    Props(wire[StatsActor]), StatsActor.name
  )
}
