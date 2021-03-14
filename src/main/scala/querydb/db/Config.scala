package querydb.db

import java.io.File

import com.typesafe.config.ConfigFactory

class Config(configPath: String) {
  private lazy val config: com.typesafe.config.Config = ConfigFactory.parseFile(new File(configPath))

  lazy val url = config.getString("jdbc.url")
  lazy val username = config.getString("jdbc.username")
  lazy val password = config.getString("jdbc.password")
}

