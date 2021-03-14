package querydb.db

case class QueryExecutor(jdbc: JDBC) {
  def execute(sql: String, fileName: String) = jdbc.withCSVWriter(sql, fileName)
}
