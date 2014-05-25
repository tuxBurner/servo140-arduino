import play.api._
import serial.SerialReader


/**
 * Created by tuxburner on 4/14/14.
 */
object Global extends GlobalSettings {



  override def onStart(app: Application) {
    try {
      SerialReader.openConnection();
    } catch {
      case _ => Logger.error("An error happened while connecting to the Arduino");
    }

  }

  override def  onStop(app: Application) {
    SerialReader.closeConnection();
  }
}
