package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class SettingsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getSettings = Action {
    Ok(views.html.index("Settings"))
  }

  def changeSettings = Action {
    Ok(views.html.index("Settings has been changed"))
  }

  def restoreDefaultSettings = Action {
    Ok(views.html.index("Default settings restored"))
  }
}