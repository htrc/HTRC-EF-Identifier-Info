package utils

import java.nio.file.Paths

import org.hathitrust.htrc.data.HtrcVolumeId

object StubbyTree {

  implicit class StringEx(s: String) {
    def takeEvery(n: Int): String = {
      require(n >= 0)
      (0 until s.length by n).map(s.charAt).mkString
    }
  }

  def stubbytreePath(htid: String): String = {
    HtrcVolumeId
      .parseUnclean(htid)
      .map { id =>
        val (libId, volId) = id.partsClean
        val stubbyPart = volId.takeEvery(3)
        Paths.get(libId, stubbyPart, s"$libId.$volId.json.bz2").toString
      }
      .get
  }

}
