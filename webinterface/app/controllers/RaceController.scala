package controllers

import neo4j.models.{NeoRaceDriverCar, ERaceType, NeoRace}
import play.api.mvc.Controller
import neo4j.Neo4JServiceProvider
import scala.collection.JavaConverters._

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
      val driver1Id = raceData.drivers.seq.headOption match {
        case Some(driver) => driver.id.intValue
        case None => 0
      }
      val car1Id = raceData.cars.seq.headOption match {
        case Some(car) => car.id.intValue
        case None => 0
      }

      Ok(views.html.race.raceSetup.render(Forms.RaceSetupFrom.fill(new RaceSetup(Forms.raceTypes.head, 10, 0, driver1Id, car1Id, Option.empty, Option.empty)), raceData, request))
  }

  /**
   * Is called when the user wants to add a new race
   * @return
   */
  def submitRaceSetup = Neo4jTransactionAction {
    implicit request =>
      Forms.RaceSetupFrom.bindFromRequest.fold(
        formWithErrors => {
          Ok(views.html.race.raceSetup.render(formWithErrors, Forms.raceData(), request))
        },
        value => {
          val neoService = Neo4JServiceProvider.get()
          val neoRace = new NeoRace()
          neoRace.raceType = ERaceType.valueOf(value.raceType)
          neoRace.laps = value.laps
          neoRace.track = neoService.trackRepo.findOne(value.track)

          neoRace.raceDriverCar1 = new NeoRaceDriverCar();
          neoRace.raceDriverCar1.driver = neoService.driverRepo.findOne(value.driver1)
          neoRace.raceDriverCar1.car = neoService.carRepo.findOne(value.car1)

          if (value.car2.isDefined == true & value.driver2.isDefined == true) {
            neoRace.raceDriverCar2 = new NeoRaceDriverCar()
            neoRace.raceDriverCar2.driver = neoService.driverRepo.findOne(value.driver2.get)
            neoRace.raceDriverCar2.car = neoService.carRepo.findOne(value.car2.get)
          }

          neoService.neoRaceRepo.save(neoRace);

          Ok("")
        }
      )
  }

  /**
   * List of unfinished races
   * @return
   */
  def unfinishedRaces = Neo4jTransactionAction {
    implicit request =>
      val races = Neo4JServiceProvider.get().neoRaceRepo.findAll().asScala.toSeq;
      Ok(views.html.race.unfinishedRaces.render(races, request))
  }

  /**
   * Deletes the selected race
   * @param id
   * @return
   */
  def deleteRace(id: Long) = Neo4jTransactionAction {
    implicit request =>
      Neo4JServiceProvider.get().neoRaceRepo.delete(id);
      Redirect(routes.RaceController.unfinishedRaces())
  }

  /**
   * Displays the view for the race
   * @param id
   * @return
   */
  def raceView(id: Long) = Neo4jTransactionAction {
    implicit request =>
      val race = Neo4JServiceProvider.get().neoRaceRepo.findOne(id);
      if (race == null) {
        BadRequest("Race not found");
      } else {
        Ok(views.html.race.raceMain.render(id,request))
      }
  }

  /**
   * Returns the json configuration
   * @param id
   * @return
   */
  def raceViewJsonConfig(id: Long) = Neo4jTransactionAction {
    implicit request =>
      val race = Neo4JServiceProvider.get().neoRaceRepo.findOne(id);
      if (race == null) {
        BadRequest("Race not found");
      } else {
        Ok(views.html.race.raceJson.render(Forms.raceAsJson(race)))
      }
  }
}
