package controllers

import play.api.mvc.{Action, Controller}
import neo4j.Neo4JServiceProvider
import org.springframework.data.domain.Sort
import neo4j.models.Track

/**
 * Created by tuxburner on 5/24/14.
 */
object TrackController extends Controller {

  /**
   * Loads all tracks from the backend
   * @return
   */
  def listTracks = Action {
    Neo4JServiceProvider.get().trackRepo.findAll(new Sort("name"));
    Ok("")
  }

  /**
   * Adds a new track
   * @param name
   * @return
   */
  def addTrack(name: String) = Action {
    val track = new Track();
    track.name = name;
    Neo4JServiceProvider.get().trackRepo.save(track);
    Ok("");
  }

}
