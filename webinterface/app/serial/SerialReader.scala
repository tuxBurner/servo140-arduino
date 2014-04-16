package serial

import jssc.SerialPort
import com.typesafe.config.ConfigFactory
import play.api.Logger

/**
 * Created by tuxburner on 4/14/14.
 */
object  SerialReader {

  var serialPort:SerialPort = new SerialPort(ConfigFactory.load().getString("serialPort"));

  def openConnection() {
    serialPort.openPort();
    serialPort.setParams(9600, 8, 1, 0);
    serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
    Logger.debug("Opened serialport at: "+serialPort.getPortName);
    serialPort.addEventListener(new SerialEvent(serialPort));
  }

  def sendData(data:String) {
    serialPort.writeString(data+"\n");
  }

  def closeConnection() {
    if(serialPort != null) {
      Logger.debug("Close serialport at: "+serialPort.getPortName);
      serialPort.closePort();
    }
  }
}
