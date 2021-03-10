package docs.http.scaladsl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.Done
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn

import scala.concurrent.Future

object Basic {

  // needed to run the route
  implicit val system = ActorSystem(Behaviors.empty, "SprayExample")
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.executionContext

  // domain model
  final case class Query(query: String, fileName: String)

  // formats for unmarshalling and marshalling
  implicit val orderFormat = jsonFormat2(Query)


  def executeQuery(query: Query): Future[Done] = {
    query match {
      case Query(a, _) => print(a)
      case _ => print("NO GOOD")
    }
    Future {
      Done
    }
  }

  def main(args: Array[String]): Unit = {
    val route =
      post {
        entity(as[Query]) { order =>
          val saved: Future[Done] = executeQuery(order)
          onSuccess(saved) { _ => // we are not interested in the result value `Done` but only in the fact that it was successful
            complete("the query is done")
          }
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}