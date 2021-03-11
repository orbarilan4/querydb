package querydb

import java.io.FileWriter
import java.sql._
import com.opencsv.CSVWriter
import scala.util.Try


/**
 * Raw structure for holding any and all fields necessary to create a JDBC connection.
 *
 * @param url      JDBC connection URL.  e.g. "jdbc:mysql://db4free.net/querydbtest"
 * @param username username for associated connection URL.  Can be null or ""
 * @param password password for associated connection URL.  Can be null or ""
 */
case class ConnectionInfo(url: String, username: String = "", password: String = "")

object JDBC {

  /**
   * Invokes the supplied function parameter with a properly created and managed JDBC Connection
   *
   * @param connInfo payload to instantiate the JDBC connection
   * @param f        function to be invoked using the managed connection
   * @tparam T return type of f.  Can be any type, including Unit
   * @return On success, will be Success[T], on failure will be Failure[Exception]
   */
  def withConnection[T](connInfo: ConnectionInfo, f: Connection => T): Try[T] = {
    val conn: Connection = DriverManager.getConnection(connInfo.url, connInfo.username, connInfo.password)

    val result: Try[T] = Try(f(conn))
    conn.close()
    result
  }

  /**
   * Invokes the supplied function parameter with a properly created and managed JDBC statement
   *
   * @param connInfo payload to instantiate the JDBC connection
   * @param f        function to be invoked using the managed statement
   * @tparam T return type of f.  Can be any type, including Unit
   * @return returns a Try Monad for the operation.  On success, will be Success[T], on failure will be Failure[Exception]
   */
  def withStatement[T](connInfo: ConnectionInfo, f: Statement => T): Try[T] = {
    def privFun(conn: Connection): T = {
      val stmt: Statement = conn.createStatement()

      // We do not need to wrap this in a Try Monad because we know we will be executing inside 'withConnection'
      // which does it for us.  Using another Try(...) here would just create a confusing second layer of structures
      // for the caller to sort through
      try {
        f(stmt)
      }
      finally {
        stmt.close()
      }
    }

    withConnection(connInfo, privFun)
  }

  /**
   * Invokes the supplied function parameter with a properly created and managed JDBC result set
   *
   * @param connInfo payload to instantiate the JDBC connection
   * @param sql      SQL Query to execute and bind to the requested result set
   * @param f        function to be invoked using the managed result set
   * @tparam T return type of f.  Can be any type, including Unit
   * @return returns a Try Monad for the operation.  On success, will be Success[T], on failure will be Failure[Exception]
   */
  def withResultSet[T](connInfo: ConnectionInfo, sql: String, f: ResultSet => T): Try[T] = {
    def privFun(stmt: Statement): T = {
      val resultSet: ResultSet = stmt.executeQuery(sql)
      try {
        f(resultSet)
      }
      finally {
        resultSet.close()
      }
    }

    withStatement(connInfo, privFun)
  }

  def withCSVWriter[T](connInfo: ConnectionInfo, sql: String, fileName: String, f: CSVWriter => T): Try[T] = {
    def privFun(resultSet: ResultSet): T = {
      val csvWriter: CSVWriter = new CSVWriter(new FileWriter(fileName))
      csvWriter.writeAll(resultSet, true)
      try {
        f(csvWriter)
      }
      finally {
        csvWriter.close()
      }
    }

    withResultSet(connInfo, sql, privFun)
  }
}
