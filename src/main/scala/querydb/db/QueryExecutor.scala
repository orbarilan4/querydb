package querydb.db

case class QueryExecutor(connectionInfo: ConnectionInfo) {
  def execute(sql: String, fileName: String) = JDBC.withCSVWriter(connectionInfo, sql, fileName)
}
