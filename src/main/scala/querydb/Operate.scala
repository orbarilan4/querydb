package querydb

object Operate extends Config {
  val connectionInfo = ConnectionInfo(url = url, username = username, password = password)

  def operate(sql: String, fileName: String) = JDBC.withCSVWriter(connectionInfo, sql, fileName)
}
