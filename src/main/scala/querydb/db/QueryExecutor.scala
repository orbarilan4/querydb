package querydb.db

/**
 * The layer responsible to execute the query on DB
 *
 * @constructor create a new QueryExecutor with a given jdbc
 * @param jdbc the jdbc instance
 */
case class QueryExecutor(jdbc: JDBC) {
  def execute(sql: String, fileName: String) = jdbc.withCSVWriter(sql, fileName)
}
