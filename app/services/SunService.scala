package services

import play.api.Play.current
import scala.concurrent.Future
import org.joda.time._
import org.joda.time.format.DateTimeFormat
import play.api.libs.ws._
import models.SunInfo
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject._

class SunService @Inject() (ws: WSClient) {
  def getSunInfo(lat: Double, lon: Double): Future[SunInfo] = {
    val responseF = ws.url("http://api.sunrise-sunset.org/json?" +
        s"lat=$lat&lng=$lon&formatted=0").get()

    responseF.map {
      response =>
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]

      val sunriseTime = DateTime.parse(sunriseTimeStr)
      val sunsetTime = DateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormat.forPattern("HH:mm:ss")
        .withZone(DateTimeZone.forID("Australia/Sydney"))

      val sunInfo = SunInfo(formatter.print(sunriseTime),
        formatter.print(sunsetTime))
      sunInfo
    }
  }
}
