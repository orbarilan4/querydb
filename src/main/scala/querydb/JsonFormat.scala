package querydb

import spray.json.DefaultJsonProtocol

/**
 * Json formats holder
 */
object JsonFormat {

  import DefaultJsonProtocol._

  implicit val orderFormat = jsonFormat2(Query)
}
