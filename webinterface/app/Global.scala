import play.api._
import serial.SerialReader


/**
 * Created by tuxburner on 4/14/14.
 */
object Global extends GlobalSettings {



  override def onStart(app: Application) {
    SerialReader.openConnection();

  }

  override def  onStop(app: Application) {
    SerialReader.closeConnection();
  }
}
