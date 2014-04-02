package sample;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.UnsupportedEncodingException;

/**
 * Created by tuxburner on 4/2/14.
 */
public class MySerialEventListener implements SerialPortEventListener {

    private StringBuffer lastValue = new StringBuffer();

    public String lastLine = new String();

    private static final byte DELIMITER = (byte) '\n';

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR()) {
            try {
                int byteCount = serialPortEvent.getEventValue();

                byte bufferIn[] = TestSerialConnect.serialPort.readBytes(byteCount);
                String stringIn = "";


                    for(final byte read : bufferIn) {
                      if(read != DELIMITER) {
                          lastValue.append((char) read);
                      } else {
                          lastLine = lastValue.toString();
                          System.out.println(lastLine);
                          lastValue = new StringBuffer();
                      }
                    }


                    // /stringIn = new String(bufferIn, "UTF-8");
                    //System.out.println(stringIn);


            } catch (SerialPortException e) {
                e.printStackTrace();
            }

        }
    }
}
