package controllers

import play.api.mvc._
import plugins.jsAnnotations.{JSRoute, JSRouteScala}
import serial.{SerialReader, SerialActor}


object ApplicationController extends Controller {

  /**
   * Action which is displayed when the user first comes to the page
   * @return
   */
  def index = Action {
    implicit request =>
      Ok(views.html.index.render((request)))
  }

  /**
   * Tells over the serial port to get the settings
   * @return
   */
  @JSRoute
  def getSerialSettings = Action {
    SerialReader.sendData("G");
    Ok;
  }

  @JSRoute
  def joinRoomWs() = WebSocket.async[String] {
    request =>
      SerialActor.attach();
  }

  /**
   * Returns the jsRoutes
   * @return
   */
  def jsRoutes = JSRouteScala.getJsRoutesResult

}