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

  /**
   * Sends the settings to the arduino
   * @param settings
   * @return
   */
  @JSRoute
  def setSerialSettings(settings: String) = Action {
    SerialReader.sendData(settings);
    Ok;
  }

  /**
   * Tells the arduino to turn off the power
   * @return
   */
  @JSRoute
  def powerOff() = Action {
    SerialReader.sendData("H");
    Ok;
  }

  /**
   * Tells the arduino to start the light sequence
   * @return
   */
  @JSRoute
  def startLightSeq() = Action {
    SerialReader.sendData("L");
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