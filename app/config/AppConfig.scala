package config

import javax.inject._
import play.api.Configuration

@Singleton
class AppConfig @Inject()(config: Configuration) {
  private val appConfig = config.get[Configuration]("ef-identifier-info")
  private val datasetsConfig = appConfig.get[Configuration]("datasets")
  val datasetsMap: Map[String, String] = datasetsConfig.keys.map(k => k -> datasetsConfig.get[String](k)).toMap

  private val releasesInfoConfig = appConfig.get[Configuration]("releases-info")
  val releasesMap: Map[String, ReleaseConfig] = releasesInfoConfig.subKeys.map { k =>
    val releaseCfg = releasesInfoConfig.get[Configuration](s""""$k"""")   // when config keys have "." (period) in them, they need to be quoted \"some.key\" when you retrieve them
    k -> ReleaseConfig.fromConfig(releaseCfg)
  }.toMap

  private val accessRightsConfig = appConfig.get[Configuration]("access-rights")
  val accessRightsMap: Map[String, String] = accessRightsConfig.keys.map(k => k -> accessRightsConfig.get[String](k)).toMap

  require(datasetsMap.values.toSet subsetOf releasesMap.keySet, "Configuration error - not all datasets have a defined release")
}
