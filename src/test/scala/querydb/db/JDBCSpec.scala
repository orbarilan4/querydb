
package querydb.db

import java.sql.{SQLException, Statement}

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FunSpec}
import org.scalatestplus.junit.JUnitRunner

import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class JDBCSpec extends FunSpec with BeforeAndAfter {
  val connectionInfo = ConnectionInfo("jdbc:mysql://db4free.net/querydbtest", "crazyuser3000", "hesoyam123123")
  val sql = "SELECT * FROM EXAMPLE"

  before {
    JDBC.withStatement(connectionInfo, (stmt: Statement) => {
      stmt.execute("CREATE TABLE EXAMPLE(ID INT PRIMARY KEY, DESCRIPTION VARCHAR)")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(0, 'Zero')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(1, 'One')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(2, 'Two')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(3, 'Three')")
      stmt.execute("INSERT INTO EXAMPLE(ID, DESCRIPTION) VALUES(4, 'Four')")
    })
  }

  describe("withConnection") {
    it("provides a valid JDBC Connection") {
      assert(JDBC.withConnection(connectionInfo, c => {
        assert(c.getMetaData != null)
      }).isSuccess)
    }

    it("projects exceptions properly") {
      assert(JDBC.withConnection(connectionInfo, _ => {
        throw new Exception
      }).isFailure)
    }

    it("invalidates the Connection outside of the provided scope") {
      JDBC.withConnection(connectionInfo, identity) match {
        case Success(conn) => {
          assert(conn != null)

          intercept[SQLException] {
            conn.getMetaData
          }
        }
        case Failure(_) => fail()
      }
    }

    describe("withStatement") {
      it("provides a valid JDBC Statement") {
        assert(JDBC.withStatement(connectionInfo, s => {
          assert(s.getQueryTimeout == 0 || s.getQueryTimeout != 0)
        }).isSuccess)
      }

      it("projects exceptions properly") {
        assert(JDBC.withStatement(connectionInfo, _ => {
          throw new Exception
        }).isFailure)
      }

      it("invalidates the Statement outside of the provided scope") {
        JDBC.withStatement(connectionInfo, identity) match {
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
      JDBC.withStatement(connectionInfo, (stmt: Statement) => {
        stmt.execute("DROP TABLE EXAMPLE")
      })
    }
  }
}
