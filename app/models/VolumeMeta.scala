package models

import org.hathitrust.htrc.data.HtrcVolumeId
import play.api.libs.json.JsValue

import scala.util.Try

object VolumeMeta {
  def fromEF(ef: JsValue): Try[VolumeMeta] = Try {
    val version = (ef \ "datePublished").as[Int].toString
    val metaJson = ef \ "metadata"
    val featuresJson = ef \ "features"

    val htid = (ef \ "htid").as[String]
    val title = (metaJson \ "title").asOpt[String]
    val contributors = (metaJson \ "contributor")
      .validateOpt[List[Contributor]]
      .recoverWith(_ => (metaJson \ "contributor").validateOpt[Contributor].map(_.map(List(_))))
      .get
    val pubDate = (metaJson \ "pubDate").asOpt[Int]
    val pubPlace = (metaJson \ "pubPlace" \ "name").asOpt[String]
    val catalogRecord = (metaJson \ "mainEntityOfPage").asOpt[List[String]].map(_.find(_.contains("/Record/")).get)
    val accessRights = (metaJson \ "accessRights").asOpt[String]
    val languages = (metaJson \ "language")
      .validateOpt[List[String]]
      .recoverWith(_ => (metaJson \ "language").validateOpt[String].map(_.map(List(_))))
      .get
    val numPages = (featuresJson \ "pageCount").as[Int]

    VolumeMeta(
      htid = htid,
      version = version,
      title = title,
      contributors = contributors,
      pubDate = pubDate,
      pubPlace = pubPlace,
      catalogRecord = catalogRecord,
      accessRights = accessRights,
      languages = languages,
      numPages = numPages
    )
  }
}

case class VolumeMeta(htid: String,
                      version: String,
                      title: Option[String],
                      contributors: Option[List[Contributor]],
                      pubDate: Option[Int],
                      pubPlace: Option[String],
                      catalogRecord: Option[String],
                      accessRights: Option[String],
                      languages: Option[List[String]],
                      numPages: Int) {
  lazy val cleanId: String = HtrcVolumeId.parseUnclean(htid).map(_.cleanId).get
}
