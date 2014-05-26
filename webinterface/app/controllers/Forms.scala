package controllers

import play.api.data._
import play.api.data.Forms._

case class Driver(name: String, imageData: String)

case class Car(carType: String, imageName: Driver)

object Forms {

  val imageNameMapping = mapping(
    "name" -> nonEmptyText,
    "imageData" -> text
  )(Driver.apply)(Driver.unapply)

  val DriverForm = Form(
    imageNameMapping
  )

  val CarForm = Form(
    mapping("carType" -> nonEmptyText,
      "imageName" -> imageNameMapping
    )
      (Car.apply)(Car.unapply)
  );
}