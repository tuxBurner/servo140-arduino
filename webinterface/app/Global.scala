import play.api._
import serial.SerialReader


/**
 * Created by tuxburner on 4/14/14.
 */
object Global extends GlobalSettings {

  val serialReader:SerialReader = new SerialReader();

  override def onStart(app: Application) {
    serialReader.openConnection();

  }

  override def  onStop(app: Application) {
    serialReader.closeConnection();
  }
}
