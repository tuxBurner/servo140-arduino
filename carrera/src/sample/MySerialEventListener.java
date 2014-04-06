package sample;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by tuxburner on 4/2/14.
 */
public class MySerialEventListener implements SerialPortEventListener {

    private StringBuffer lastValue = new StringBuffer();

    public String lastLine = new String();

    private static final byte DELIMITER = (byte) '\n';
    private SerialPort serialPort;

    public MySerialEventListener(SerialPort serialPort) {

        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR()) {
            try {
                int byteCount = serialPortEvent.getEventValue();

                byte bufferIn[] = serialPort.readBytes(byteCount);

                for (final byte read : bufferIn) {
                    if (read != DELIMITER) {
                        lastValue.append((char) read);
                    } else {
                        lastLine = lastValue.toString();
                        Main.root.fireEvent(new SerialLineEvent(lastLine));
                        System.out.println(lastLine);
                        lastValue = new StringBuffer();
                    }
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
