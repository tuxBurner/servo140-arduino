package controllers

import neo4j.Neo4JServiceProvider
import neo4j.models.{NeoCar, NeoDriver, NeoTrack, NeoTrackParts}
import org.springframework.data.domain.Sort
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json._
import play.api.templates.Html

import scala.collection.JavaConverters._

/**
 * Simple mapping for an entity from a form containing name and an imager
 * @param name
 * @param imageData
 */
case class ImageName(name: String, imageData: String)

/**
 * Mapping for a track from a http req
 * @param name
 */
case class Track(name: String)

/**
 * Mapping for a car
 * @param carType
 * @param imageName
 */
case class Car(carType: String, imageName: ImageName)

/**
 * Mapping for the amount of parts the user has
 * @param straight
 * @param dblStraight
 * @param fourthStraight
 * @param thirdStraight
 * @param curve90
 * @param curve45
 * @param curve452
 * @param connectStraight
 */
case class TrackParts(straight: Int, dblStraight: Int, fourthStraight: Int, thirdStraight: Int, curve90: Int, curve45: Int, curve452: Int, connectStraight: Int)

/**
 * Class which holds all data for the race setup
 * @param tracks
 * @param drivers
 * @param cars
 */
case class RaceData(tracks: Seq[NeoTrack], drivers: Seq[NeoDriver], cars: Seq[NeoCar]);

/**
 * Class which holds the data for the race setup
 */
case class RaceSetup(raceType: String, rounds: Int, driver1: Int, car1: Int, driver2: Option[Int], car2: Option[Int]);



object Forms {

  val raceTypes = Seq("Vs","VsTime","Time")

  val imageNameMapping = mapping(
    "name" -> nonEmptyText,
    "imageData" -> text
  )(ImageName.apply)(ImageName.unapply)

  val ImageNameForm = Form(
    imageNameMapping
  )

  val CarForm = Form(
    mapping("carType" -> nonEmptyText,
      "imageName" -> imageNameMapping
    )
      (Car.apply)(Car.unapply)
  )

  val TrackForm = Form(
    mapping("name" -> nonEmptyText())
      (Track.apply)(Track.unapply)
  )

  val TrackPartsForm = Form(
    mapping("straight" -> number(0),
      "dblStraight" -> number(0),
      "fourthStraight" -> number(0),
      "thirdStraight" -> number(0),
      "curve90" -> number(0),
      "curve45" -> number(0),
      "curve452" -> number(0),
      "connectStraight" -> number(0))
      (TrackParts.apply)(TrackParts.unapply)
  )

  val RaceSetupFrom = Form(
    mapping("raceType" -> nonEmptyText(),
      "rounds" -> number(1),
      "driver1" -> number,
      "car1" -> number,
      "driver2" -> optional(number),
      "car2" -> optional(number)
    )
      (RaceSetup.apply)(RaceSetup.unapply)
  )

  def neoTrackPartsToTrackParts(neoTrackParts: NeoTrackParts): TrackParts = {
    TrackParts.apply(neoTrackParts.straight, neoTrackParts.dblStraight, neoTrackParts.fourthStraight, neoTrackParts.thirdStraight, neoTrackParts.curve90, neoTrackParts.curve45, neoTrackParts.curve452, neoTrackParts.connectStraight)
  }

  implicit val neoDriverWrites = new Writes[NeoDriver] {
    def writes(driver: NeoDriver) = Json.obj(
      "id" -> driver.id.longValue(),
      "name" -> driver.name,
      "img" -> driver.hasPicture.booleanValue()
    )
  }

  implicit val neoCarWrites = new Writes[NeoCar] {
    def writes(car: NeoCar) = Json.obj(
      "id" -> car.id.longValue(),
      "name" -> car.name,
      "img" -> car.hasPicture.booleanValue()
    )
  }

  implicit val neoTrackWrites = new Writes[NeoTrack] {
    def writes(track: NeoTrack) = Json.obj(
      "id" -> track.id.longValue(),
      "name" -> track.name
    )
  }

  implicit val raceDataWriter = new Writes[RaceData] {
    def writes(raceData: RaceData) = Json.obj(
      "drivers" -> raceData.drivers,
      "cars" -> raceData.cars,
      "tracks" -> raceData.tracks
    )
  }


  /**
   * Prepars all the data as json for the race settings panel
   * @return
   */
  def raceData(): RaceData = {
    val drivers = Neo4JServiceProvider.get().driverRepo.findAll(new Sort("name")).iterator().asScala.toSeq;
    val cars = Neo4JServiceProvider.get().carRepo.findAll(new Sort("name")).iterator().asScala.toSeq;
    val tracks = Neo4JServiceProvider.get().trackRepo.findAll(new Sort("name")).iterator().asScala.toSeq;

    RaceData.apply(tracks, drivers, cars)
  }

  /**
   * Prepars all the data as json for the race settings panel
   * @return
   */
  def jsonRaceSettings(): Html = {
    val json = Json.toJson(raceData);
    new Html(new StringBuilder(json.toString()))
  }
}