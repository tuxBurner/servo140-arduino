package controllers

import neo4j.models.NeoRace
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
      val raceData = Forms.raceData()
      val driver1Id = raceData.drivers.seq.headOption match { case Some(driver) => driver.id.intValue case None => 0 }
      val car1Id = raceData.cars.seq.headOption match { case Some(car) => car.id.intValue
                                                         case None => 0 }
      Ok(views.html.race.raceSetup.render(Forms.RaceSetupFrom.fill(new RaceSetup(Forms.raceTypes.head,10,driver1Id,car1Id,Option.empty, Option.empty)),raceData,request))
  }

  def submitRaceSetup = Neo4jTransactionAction {
    implicit request =>
      Forms.RaceSetupFrom.bindFromRequest.fold(
        formWithErrors => {
          Ok(views.html.race.raceSetup.render(formWithErrors,Forms.raceData(),request))
        } ,
        value => {
          NeoRace
          Ok("")
        }
      )
  }
}
