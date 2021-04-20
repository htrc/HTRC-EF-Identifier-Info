import play.api.libs.json._

package object models {
  implicit val contributorFormat: OFormat[Contributor] = Json.format[Contributor]
  implicit val volumeMetaFormat: OFormat[VolumeMeta] = Json.format[VolumeMeta]
}
