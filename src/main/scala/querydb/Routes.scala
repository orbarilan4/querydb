package querydb

import akka.Done
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future
import akka.actor.typed.ActorSystem
import akka.util.Timeout
import querydb.db.QueryExecutor

class Routes(queryExecutor: QueryExecutor)(implicit val system: ActorSystem[_]) {

  import system.executionContext
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormat._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("app.routes.ask-timeout"))

  def operate(query: Query): Future[Done] = {
    query match {
      case Query(sql, fileName) => queryExecutor.execute(sql, fileName)
      case _ => system.log.error("New query received, but schema's query was incorrect")
    }
    Future {
      Done
    }
  }

  val route =
    post {
      entity(as[Query]) { query =>
        val request: Future[Done] = operate(query)
        onSuccess(request) { _ =>
          system.log.info("New query: '{}' received, the result has saved to '{}'", query.query, query.fileName)
          complete("The query was performed successfully !")
        }
      }
    }
}
