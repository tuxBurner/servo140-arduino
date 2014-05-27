package controllers

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
}