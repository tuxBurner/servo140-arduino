package controllers

import play.api.mvc.{Action, Controller}
import neo4j.Neo4JServiceProvider
import org.springframework.data.domain.Sort
import neo4j.models.Track
import plugins.jsAnnotations.JSRoute
import scala.collection.JavaConverters._

/**
 * Created by tuxburner on 5/24/14.
 */
object DataController extends Controller {


  /**
   * The main view when the user clicks on the data option
   * @return
   */
  def mainDataView = Action {
    implicit request =>
      Ok(views.html.data.mainview.render((request)));
  }

  /**
   * Loads all tracks from the backend
   * @return
   */
  @JSRoute
  def listTracks = Action {
    Neo4JServiceProvider.get().trackRepo.findAll(new Sort("name"));
    Ok("")
  }

  /**
   * Adds a new track
   * @param name
   * @return
   */
  @JSRoute
  def addTrack(name: String) = Action {
    val track = new Track();
    track.name = name;
    Neo4JServiceProvider.get().trackRepo.save(track);
    Ok("");
  }

  /**
   * Lists all drivers
   * @return
   */
  @JSRoute
  def listDrivers = Action {
    val drivers = Neo4JServiceProvider.get().driverRepo.findAll(new Sort("name")).iterator().asScala.toList;
    Ok(views.html.data.driverView(drivers))
  }

  /**
   * Displays the add driver form
   * @return
   */
  @JSRoute
  def displayAddDriver = Action {
    Ok(views.html.data.addDriver())
  }

  @JSRoute
  def addDriver = Action {
    Redirect(routes.DataController.mainDataView).flashing(BaseController.FLASH_SUCCESS_KEY -> "Driver added.");
  }

}
