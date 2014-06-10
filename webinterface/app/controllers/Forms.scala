package controllers

import neo4j.models.NeoTrackParts
import play.api.data._
import play.api.data.Forms._

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


object Forms {

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

  def neoTrackPartsToTrackParts(neoTrackParts: NeoTrackParts): TrackParts  = {
    TrackParts.apply(neoTrackParts.straight,neoTrackParts.dblStraight,neoTrackParts.fourthStraight,neoTrackParts.thirdStraight,neoTrackParts.curve90,neoTrackParts.curve45,neoTrackParts.curve452,neoTrackParts.connectStraight)
  }
}