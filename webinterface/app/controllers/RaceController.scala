package controllers

import play.api.mvc.Controller

/**
 * Created by tuxburner on 6/25/14.
 */
object RaceController extends Controller {
  /**
   * Displays the race setup page
   * @return
   */
  def raceSetup = Neo4jTransactionAction {
    implicit request =>
      Ok(views.html.race.raceSetup.render(Forms.raceData(),request))
  }
}