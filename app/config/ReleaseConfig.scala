package config

import play.api.Configuration

case class ReleaseConfig(path: String, baseUrl: Option[String], rsync: Option[String])

object ReleaseConfig {
  def fromConfig(config: Configuration): ReleaseConfig =
    ReleaseConfig(
      config.get[String]("path"),
      config.getOptional[String]("baseurl"),
      config.getOptional[String]("rsync")
    )
}
