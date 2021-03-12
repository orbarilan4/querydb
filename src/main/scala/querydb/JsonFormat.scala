package querydb

import spray.json.DefaultJsonProtocol

object JsonFormat {

  import DefaultJsonProtocol._

  implicit val orderFormat = jsonFormat2(Query)
}
