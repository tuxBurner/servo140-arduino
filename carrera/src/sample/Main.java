package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {


    public static Parent root;


    @Override
    public void start(Stage primaryStage) throws Exception{


        root = FXMLLoader.load(getClass().getResource("MainPanel.fxml"));

        primaryStage.setTitle("Carrera Manager");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        /**
         * Close the serial communication when the application is closed.
         */
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                SerialHelper.get().closePort();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
