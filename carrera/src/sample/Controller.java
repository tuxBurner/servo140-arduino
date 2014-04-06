package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Section;
import jssc.SerialPortList;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {


    private ObservableList<String> portList = FXCollections.observableArrayList();


    /**
     * Detects the available com ports
     */
    private void detectPort() {
        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, name);
            portList.add(name);
        }
    }

    @FXML
    private Gauge carAGauge;

    @FXML
    private ComboBox<String> comPortBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carAGauge.setTitle("Car A");
        carAGauge.setMinValue(0);
        carAGauge.setMaxValue(255);
        carAGauge.addSection(new Section(0,25, Color.AQUA));
        carAGauge.addSection(new Section(25,50, Color.RED));

        carAGauge.addEventHandler(SerialLineEvent.SERIAL_LINE_READ, new EventHandler<SerialLineEvent>() {
            @Override
            public void handle(SerialLineEvent serialLineEvent) {

            }
        });

        detectPort();
        comPortBox.setItems(portList);
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
