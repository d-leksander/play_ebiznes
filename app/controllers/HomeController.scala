package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import javax.inject._
import play.api.Environment
import play.api.i18n.I18nSupport
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.IndexRenderService
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(components: ControllerComponents,
                               silhouette: Silhouette[DefaultEnv],
                               environment: Environment,
                               ws: WSClient,
                               indexRenderService: IndexRenderService,
                               authInfoRepository: AuthInfoRepository)
                              (implicit ec: ExecutionContext) extends AbstractController(components) with I18nSupport {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index("ok"))
  }

  def signOut: Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)
  }
}