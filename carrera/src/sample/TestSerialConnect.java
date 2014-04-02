package sample;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Created by tuxburner on 4/2/14.
 */
public class TestSerialConnect {

    public static SerialPort serialPort;



    public static void main(String[] args) {
        serialPort = new SerialPort("/dev/ttyACM0");
        try {
            System.out.println("Port opened: " + serialPort.openPort());
            System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0));

            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);

            //System.out.println("\"Hello World!!!\" successfully writen to port: " + serialPort.writeBytes("Hello World!!!".getBytes()));
            serialPort.addEventListener(new MySerialEventListener());
            while (true) {

            }

            //System.out.println("Port closed: " + serialPort.closePort());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        } finally {
            try {
                System.out.println("Port closed: " + serialPort.closePort());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
