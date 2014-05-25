package controllers

import play.api.data._
import play.api.data.Forms._

case class Driver(name: String, imageData: String)

object Forms {
  val DriverForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "imageData" -> text
    )(Driver.apply)(Driver.unapply)
  )
}