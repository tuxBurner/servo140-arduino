package sample;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.commons.lang3.StringUtils;

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
                        //fireEvent(new SerialLineEvent(lastLine));
                        final String[] split = StringUtils.split(lastLine,',');

                        if(split.length == 4) {
                            try {
                                Main.controller.carAGauge.setValue(Double.valueOf(split[2]));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /*Gauge gauge = (Gauge) Main.controller.;
                            gauge.setValue(Double.valueOf(split[2]));
                            boolean steerRight = Boolean.valueOf(split[3]);*/
                          /*  if(steerRight == true) {
                                gauge.setTrend(Gauge.Trend.UP);
                            } else {
                                gauge.setTrend(Gauge.Trend.DOWN);
                            }*/
                        }
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
