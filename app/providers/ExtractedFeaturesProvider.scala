package providers

import java.nio.file.{Path, Paths}

import com.google.inject.ImplementedBy
import models.VolumeMeta

import scala.util.Try

@ImplementedBy(classOf[StubbytreeExtractedFeaturesProvider])
trait ExtractedFeaturesProvider {

  def getRelativePath(htid: String): String

  def getMetadata(efPath: Path): Try[VolumeMeta]

  def getExtractedFeaturesPath(dir: String, htid: String): Path = Paths.get(dir, getRelativePath(htid))

}
