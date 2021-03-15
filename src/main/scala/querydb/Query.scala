package querydb

/**
 * Raw structure for holding any and all fields necessary to create a Query request.
 *
 * @constructor create a new Query with a given query, filename
 * @param query    the actual query
 * @param fileName the output file name
 */
final case class Query(query: String, fileName: String)
