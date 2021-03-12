package docs.http.scaladsl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import querydb.Routes
import scala.util.{Failure, Success}

object Main {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)
    bindingFuture.onComplete {
          case Success(binding) =>
            val address = binding.localAddress
            system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
          case Failure(ex) =>
            system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
            system.terminate()
        }
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "JDBC-Microservice")
    val routes = new Routes

    startHttpServer(routes.route)
  }
}