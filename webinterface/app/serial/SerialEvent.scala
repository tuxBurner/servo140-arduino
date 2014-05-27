package serial

import jssc.{SerialPort, SerialPortEvent, SerialPortEventListener}
import play.api.Logger

/**
 * Created by tuxburner on 4/14/14.
 */
class SerialEvent(serialPort: SerialPort) extends SerialPortEventListener {
  val DELIMITER: Byte = '\n';
  var lastValue: String = "";

  override def serialEvent(serialPortEvent: SerialPortEvent) {
    if (serialPortEvent.isRXCHAR()) {
      val byteCount = serialPortEvent.getEventValue();
      val bufferIn = serialPort.readBytes(byteCount);
      for (read <- bufferIn) {
        if (read != DELIMITER) {
          lastValue += read.toChar;
        } else {
          Logger.debug(lastValue);
          // send message to the serial ws
          SerialActor.roomActor ! SendMessage(lastValue);
          lastValue = "";
        }
      }
    }
  }
}
