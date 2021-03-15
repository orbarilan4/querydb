package querydb.db

import java.io.File
import java.sql.Statement

import org.junit.runner.RunWith
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.BeforeAndAfter
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class QueryExecutorSpec extends AnyFunSpec with BeforeAndAfter {
  val connectionInfo = ConnectionInfo("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1")
  val jdbc = JDBC(connectionInfo)
  val queryExecutor = QueryExecutor(jdbc)
  val sql = "SELECT * FROM EXAMPLE"
  val fileName = "fileName.csv"

  before {
    println("before")
    jdbc.withStatement((stmt: Statement) => {
      stmt.execute("CREATE TABLE EXAMPLE(ID INT PRIMARY KEY, DESCRIPTION VARCHAR)")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(0, 'Zero')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(1, 'One')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(2, 'Two')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(3, 'Three')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(4, 'Four')")
    })
  }

  describe("execute") {
    it("provides a valid sql query") {
      assert(queryExecutor.execute(sql, fileName).isSuccess)
    }
    it("provides an invalid sql query") {
      assert(queryExecutor.execute(sql + "invalid", fileName).isFailure)
    }
  }

  after {
    jdbc.withStatement((stmt: Statement) => {
      stmt.execute("DROP TABLE EXAMPLE")
    })
    val file = new File("output/" + fileName);
    if (file.exists()) {
      file.delete()
    }
  }
}