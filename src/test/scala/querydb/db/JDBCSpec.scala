
package querydb.db

import java.sql.{SQLException, Statement}

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FunSpec}
import org.scalatestplus.junit.JUnitRunner

import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class JDBCSpec extends FunSpec with BeforeAndAfter {
  val connectionInfo = ConnectionInfo(
    "jdbc:mysql://db4free.net/querydbtest",
    "crazyusers000",
    "hesoyam123123")
  val sql = "SELECT * FROM EXAMPLE"
  val jdbc = JDBC(connectionInfo)
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
        assert(jdbc.withStatement( _ => {
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

    after {
      jdbc.withStatement((stmt: Statement) => {
        stmt.execute("DROP TABLE EXAMPLE")
      })
    }
}
