package querydb

import akka.Done
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future
import akka.actor.typed.ActorSystem
import akka.util.Timeout
import querydb.db.OperateQuery

class Routes(implicit val system: ActorSystem[_]) {
  import system.executionContext
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormat._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("app.routes.ask-timeout"))

  def executeQuery(query: Query): Future[Done] = {
    query match {
      case Query(a, b) => OperateQuery.operate(a, b)
      case _ => print("asd")
    }
    Future {
      Done
    }
  }

  val route =
    post {
      entity(as[Query]) { order =>
        val query: Future[Done] = executeQuery(order)
        onSuccess(query) { _ =>
          complete("the query is done")
        }
      }
    }
}
