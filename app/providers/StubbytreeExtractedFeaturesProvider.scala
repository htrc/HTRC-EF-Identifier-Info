package providers

import java.nio.file.{Files, Path}

import models.VolumeMeta
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import play.api.libs.json.Json
import utils.StubbyTree.stubbytreePath
import scala.util.Using

import scala.util.Try

class StubbytreeExtractedFeaturesProvider extends ExtractedFeaturesProvider {

  override def getRelativePath(htid: String): String = stubbytreePath(htid)

  override def getMetadata(efPath: Path): Try[VolumeMeta] = {
    Using(new BZip2CompressorInputStream(Files.newInputStream(efPath)))(Json.parse)
      .flatMap(VolumeMeta.fromEF)
  }

}
