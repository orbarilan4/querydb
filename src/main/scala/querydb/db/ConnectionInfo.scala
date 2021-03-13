package querydb.db

/**
 * Raw structure for holding any and all fields necessary to create a JDBC connection.
 *
 * @param url      JDBC connection URL.  e.g. "jdbc:mysql://db4free.net/querydbtest"
 * @param username username for associated connection URL.  Can be null or ""
 * @param password password for associated connection URL.  Can be null or ""
 */
case class ConnectionInfo(url: String, username: String = "", password: String = "")
