package filters

import play.api.mvc.{Result, RequestHeader, Filter}
import akka.actor.ActorSystem
import akka.stream.Materializer
import scala.concurrent.Future
import actors.StatsActor

class StatsFilter(actorSystem: ActorSystem, implicit val mat: Materializer)
  extends Filter {

  override def apply(nextFilter: (RequestHeader) => Future[Result])
    (header: RequestHeader): Future[Result] = {
      actorSystem.actorSelection(StatsActor.path) ! StatsActor.RequestReceived
      nextFilter(header)
    }

}
