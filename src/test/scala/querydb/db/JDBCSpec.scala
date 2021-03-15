
package querydb.db

import java.io.File
import java.sql.{SQLException, Statement}
import org.scalatest.funspec.AnyFunSpec
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatestplus.junit.JUnitRunner
import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class JDBCSpec extends AnyFunSpec with BeforeAndAfter {
  val connectionInfo = ConnectionInfo("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1")
  val jdbc = JDBC(connectionInfo)
  val sql = "SELECT * FROM EXAMPLE"
  val fileName = "fileName.csv"
  before {
    jdbc.withStatement((stmt: Statement) => {
      stmt.execute("CREATE TABLE EXAMPLE(ID INT PRIMARY KEY, DESCRIPTION VARCHAR)")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(0, 'Zero')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(1, 'One')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(2, 'Two')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(3, 'Three')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(4, 'Four')")
    })
  }

  describe("withStatement") {
    it("provides a valid JDBC Statement") {
      assert(jdbc.withStatement(s => {
        assert(s.getQueryTimeout == 0 || s.getQueryTimeout != 0)
      }).isSuccess)
    }

    it("projects exceptions properly") {
      assert(jdbc.withStatement(_ => {
        throw new Exception
      }).isFailure)
    }

    it("invalidates the Statement outside of the provided scope") {
      jdbc.withStatement(identity) match {
        case Success(stmt) => {
          assert(stmt != null)

          intercept[SQLException] {
            stmt.getQueryTimeout
          }
        }
        case Failure(_) => fail()
      }
    }
  }

  describe("withResultSet") {
    it("provides a valid JDBC ResultSet") {
      assert(jdbc.withResultSet(sql, rs => {
        assert(rs.next())
      }).isSuccess)
    }

    it("projects exceptions properly") {
      assert(jdbc.withResultSet(sql + "invalid", _ => ()).isFailure)
    }

    it("invalidates the ResultSet outside of the provided scope") {
      jdbc.withResultSet(sql, identity) match {
        case Success(resultSet) => {
          assert(resultSet != null)

          intercept[SQLException] {
            resultSet.next()
          }
        }
        case Failure(_) => fail()
      }
    }
  }

  describe("withCSVWriter") {
    it("provides a valid sql query") {

      assert(jdbc.withCSVWriter(sql, fileName).isSuccess)
    }

    it("provides an invalid sql query") {
      assert(jdbc.withCSVWriter(sql + "invalid", fileName).isFailure)
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
