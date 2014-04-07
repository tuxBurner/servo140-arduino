package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.Section;
import jssc.SerialPortList;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    private ObservableList<String> portList = FXCollections.observableArrayList();


    /**
     * Detects the available com ports
     */
    private void detectPort() {
        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }
    }

    @FXML
    public Gauge carAGauge;

    @FXML
    private ComboBox<String> comPortBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        detectPort();
        comPortBox.setItems(portList);
        if(portList.size() == 1) {
            comPortBox.setValue(portList.get(0));
        }

        carAGauge.setPointerType(Gauge.PointerType.TYPE4);
        carAGauge.setMajorTickmarkType(Gauge.TickmarkType.TRIANGLE);

        carAGauge.setTitle("Car A");
        carAGauge.setMinValue(0);
        carAGauge.setMaxValue(250);
        carAGauge.setMajorTickSpacing(25);
        carAGauge.addArea(new Section(0, 25, Color.AQUA));
        carAGauge.addSection(new Section(0, 25, Color.AQUA));
        carAGauge.addSection(new Section(25,50, Color.RED));
        carAGauge.addArea(new Section(25, 50, Color.RED));
        carAGauge.setBackgroundDesign(Gauge.BackgroundDesign.CARBON);
        carAGauge.setKnobDesign(Gauge.KnobDesign.METAL);
        carAGauge.setTrendVisible(true);
        carAGauge.setAnimationDuration(400);
        carAGauge.setThreshold(225);
        carAGauge.setThresholdVisible(true);
        carAGauge.addEventHandler(SerialLineEvent.SERIAL_LINE_READ, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                System.out.println(event.getEventType());
            }
        });




    }

    /**
     * is called when the user selects a comport
     * @param actionEvent
     */
    public void selectComPort(ActionEvent actionEvent) {
        final String value = comPortBox.getValue();
        if(StringUtils.isBlank(value) == false) {
            SerialHelper.get().connectToSerialPort(value);
        } else {
            SerialHelper.get().closePort();
        }
    }
}
