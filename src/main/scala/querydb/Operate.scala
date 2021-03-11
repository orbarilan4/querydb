package querydb

import com.opencsv.CSVWriter

object Operate extends Config {
  val connectionInfo = ConnectionInfo(url = url, username = username, password = password)

  def func(c: CSVWriter) = true

  def operate(sql: String, fileName: String) = JDBC.withCSVWriter(connectionInfo, sql, fileName, func)
}
