package controllers

import java.nio.file.NoSuchFileException

import config.AppConfig
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import providers.ExtractedFeaturesProvider

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class ExtractedFeaturesController @Inject()(extractedFeaturesProvider: ExtractedFeaturesProvider,
                                            volInfo: views.html.volInfo,
                                            config: AppConfig,
                                            val controllerComponents: ControllerComponents)
                                           (implicit ec: ExecutionContext) extends BaseController {

  def volInfo(date: String, htid: String): Action[AnyContent] =
    Action { implicit req =>
      config.datasetsMap.get(date) match {
        case Some(dataset) =>
          val release = config.releasesMap(dataset)
          val efPath = extractedFeaturesProvider.getExtractedFeaturesPath(release.path, htid)
          extractedFeaturesProvider.getMetadata(efPath) match {
            case Success(meta) if meta.version == date =>
              render {
                case Accepts.Html() => Ok(volInfo(date, meta, config.accessRightsMap, release))
                case Accepts.Json() => Ok(Json.toJson(meta))
              }

            case Success(meta) => NotFound(s"Version mismatch - newer version found ${meta.version}")

            case Failure(_: NoSuchFileException) => NotFound
            case Failure(e) => InternalServerError(e.toString)
          }

        case None => NotFound
      }
    }

}
