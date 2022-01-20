package controllers

import models.{Contributor, VolumeMeta}
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestData
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import providers.ExtractedFeaturesProvider

import java.nio.file.Paths
import scala.util.Success


class ExtractedFeaturesControllerSpec extends PlaySpec with GuiceOneAppPerTest with MockFactory with Injecting {
  private val _htid = "mdp.123456789"
  private val _date = "99999999"
  private val _dirPath = "/fake/dir"
  private val _stubbyPath = Paths.get(_dirPath, "mdp/147/mdp.123456789.json.bz2")
  private val _baseUrl = "https://fake.baseurl"
  private val _rsyncBase = "rsync://fake.spec"
  private val _volumeMeta = VolumeMeta(
    htid = _htid,
    version = _date,
    title = Some("Some title"),
    contributors = Some(List(Contributor("https://fake.url/john_contributor", "John Contributor"))),
    pubDate = Some(1901),
    pubPlace = Some("USA"),
    catalogRecord = Some(s"https://catalog.url/${_htid}"),
    accessRights = Some("pd"),
    languages = Some(List("en")),
    numPages = 100
  )

  "HomeController GET" should {
    "retrieve the volume metadata as HTML" in {
      val request = FakeRequest(GET, s"/${_date}/${_htid}")
      val volInfo = route(app, request).get

      status(volInfo) mustBe OK
      contentType(volInfo) mustBe Some("text/html")

      val content = contentAsString(volInfo)
      content must include(_htid)
      content must include(_volumeMeta.title.get)
      content must include(_date)
      content must include(_baseUrl)
      content must include(_rsyncBase)
      content must include(_volumeMeta.numPages.toString)
      content must include(_volumeMeta.catalogRecord.get)
      content must include(_volumeMeta.pubDate.get.toString)
      content must include(_volumeMeta.pubPlace.get)
      content must include(_volumeMeta.contributors.get.head.id)
      content must include(_volumeMeta.contributors.get.head.name)
    }

    "retrieve the volume metadata as JSON" in {
      val request = FakeRequest(GET, s"/${_date}/${_htid}").withHeaders("Accept" -> "application/json")
      val volInfo = route(app, request).get

      status(volInfo) mustBe OK
      contentType(volInfo) mustBe Some("application/json")

      val content = contentAsJson(volInfo)
      (content \ "htid").as[String] mustEqual _htid
      (content \ "version").as[String] mustEqual _date
      (content \ "title").asOpt[String] mustEqual _volumeMeta.title
      (content \ "contributors").asOpt[List[Contributor]] mustEqual _volumeMeta.contributors
      (content \ "pubDate").asOpt[Int] mustEqual _volumeMeta.pubDate
      (content \ "pubPlace").asOpt[String] mustEqual _volumeMeta.pubPlace
      (content \ "catalogRecord").asOpt[String] mustEqual _volumeMeta.catalogRecord
      (content \ "accessRights").asOpt[String] mustEqual _volumeMeta.accessRights
      (content \ "languages").asOpt[List[String]] mustEqual _volumeMeta.languages
      (content \ "numPages").as[Int] mustEqual _volumeMeta.numPages
    }
  }

  override def newAppForTest(testData: TestData): Application =
    new GuiceApplicationBuilder()
      .configure(
        s"ef-identifier-info.datasets.${_date}" -> "unittest",
        "ef-identifier-info.releases-info.unittest" -> Map(
          "path" -> _dirPath,
          "baseurl" -> _baseUrl,
          "rsync" -> _rsyncBase
        )
      )
      .overrides(bind[ExtractedFeaturesProvider].toInstance(createMockedMetadataProvider()))
      .build()

  private def createMockedMetadataProvider(): ExtractedFeaturesProvider = {
    val mockedMetadataProvider = mock[ExtractedFeaturesProvider]
    (mockedMetadataProvider.getExtractedFeaturesPath _).expects(_dirPath, _htid).returning(_stubbyPath).once()
    (mockedMetadataProvider.getMetadata _).expects(_stubbyPath).returning(Success(_volumeMeta)).once()
    (mockedMetadataProvider.getRelativePath _).expects(*).anyNumberOfTimes()

    mockedMetadataProvider
  }

}
