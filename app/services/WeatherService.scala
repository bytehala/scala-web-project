package services

import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WeatherService (ws: WSClient) {
  def getTemperature(lat: Double, lon: Double): Future[Double] = {

    val url = "http://api.openweathermap.org/data/2.5/" +
        s"weather?lat=$lat&lon=$lon&units=metric" +
        "&APPID=c10d246c7bbf1e228c6c0c9bfbb44760"
    println(url)
    val weatherResponseF = ws.url(url).get()

    weatherResponseF.map {
      weatherResponse =>
      val weatherJson = weatherResponse.json
      val temperature = (weatherJson \ "main" \ "temp").as[Double]
      temperature
    }
  }
}
