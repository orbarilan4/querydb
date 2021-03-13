package querydb.db

import java.io.File

import com.typesafe.config.ConfigFactory

class Config(configPath: String) {
  private val config: com.typesafe.config.Config = ConfigFactory.parseFile(new File(configPath))

  val url = config.getString("jdbc.url")
  val username = config.getString("jdbc.username")
  val password = config.getString("jdbc.password")
}

