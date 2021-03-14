package querydb

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import querydb.db.{Config, ConnectionInfo, JDBC, QueryExecutor}

import scala.util.{Failure, Success}

object Main {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(routes)
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
    if (args.length == 0) {
      println("Error: DB configuration file-path not found")
      sys.exit(1)
    }
    else {
      implicit val system = ActorSystem(Behaviors.empty, "JDBC-Microservice")
      val config = new Config(args(0))
      val jdbc = JDBC(ConnectionInfo(config.url, config.username, config.password))
      val queryExecutor = QueryExecutor(jdbc)
      val routes = new Routes(queryExecutor)

      startHttpServer(routes.route)
    }
  }
}