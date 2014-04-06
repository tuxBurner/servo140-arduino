package sample;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Controlls the serial communication stuff.
 * Created by tuxburner on 4/6/14.
 */
public class SerialHelper {

    /**
     * Static singleton instance
     */
    private final static SerialHelper INSTANCE = new SerialHelper();

    /**
     * The current connected serial port
     */
    private SerialPort serialPort;


    /**
     * private constructor for singleton instance
     */
    private SerialHelper() {
        super();
    }

    /**
     * Getter for the singleton instance.
     * @return
     */
    public static SerialHelper get() {
        return INSTANCE;
    }


    /**
     * Connects to a serial port
     * @param port
     */
    public void connectToSerialPort(final String port) {

        // close the port if it is already open
        if(serialPort != null) {
            closePort();
        }

        // create a new serial port
        serialPort = new SerialPort(port);

        try {
            System.out.println("Port opened: " + serialPort.openPort());
            System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0));
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.addEventListener(new MySerialEventListener(serialPort));
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Writes the given message to the serial port
     *
     * @param message
     */
    public void writeToSerial(final String message) {
        if (serialPort == null) {
            return;
        }

        try {
            serialPort.writeBytes(message.getBytes());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the port
     */
    public void closePort() {
        if (serialPort == null) {
            return;
        }

        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        } finally {
            serialPort = null;
        }
    }

}
