package querydb.db

import java.io.{File, FileWriter}
import java.sql._

import com.opencsv.CSVWriter

import scala.util.Try

case class JDBC (connInfo: ConnectionInfo){
  /**
   *  Created and managed JDBC Connection
   */
   lazy val conn: Try[Connection] = Try{DriverManager.getConnection(connInfo.url, connInfo.username, connInfo.password)}

  /**
   * Invokes the supplied function parameter with a properly created and managed JDBC statement
   *
   * @param connInfo payload to instantiate the JDBC connection
   * @param f        function to be invoked using the managed statement
   * @tparam T return type of f.  Can be any type, including Unit
   * @return returns a Try Monad for the operation.  On success, will be Success[T], on failure will be Failure[Exception]
   */
  def withStatement[T](f: Statement => T): Try[T] = {
      val stmt: Statement = conn.get.createStatement()

      // We do not need to wrap this in a Try Monad because we know we will be executing inside 'withConnection'
      // which does it for us.  Using another Try(...) here would just create a confusing second layer of structures
      // for the caller to sort through
      val result: Try[T] = Try(f(stmt))
      stmt.close()
      result
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
  def withResultSet[T](sql: String, f: ResultSet => T): Try[T] = {
    def privFun(stmt: Statement): T = {
      val resultSet: ResultSet = stmt.executeQuery(sql)
      try {
        f(resultSet)
      }
      finally {
        resultSet.close()
      }
    }

    withStatement(privFun)
  }

  def withCSVWriter(sql: String, fileName: String): Try[Int] = {
    def privFun(resultSet: ResultSet): Int = {
      val filePath = new File("output/" + fileName)
      val parent = filePath.getParentFile()
      if (!parent.exists()) {
          parent.mkdir()
      }
      val csvWriter: CSVWriter = new CSVWriter(new FileWriter(filePath))
      try {
        csvWriter.writeAll(resultSet, true)
      }
      finally {
        csvWriter.close()
      }
    }

    withResultSet(sql, privFun)
  }
}
