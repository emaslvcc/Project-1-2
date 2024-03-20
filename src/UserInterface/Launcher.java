package UserInterface;

import Calculators.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import DataManagers.PostCode;
import java.util.Scanner;

public class Launcher extends Application {
    private double walkingTime;
    private double cyclingTime;
    private GenericCalculator timeCalc;
    public static void main(String[] args) {

        //call a method to start a program
        launch(args);
    }

    public void start(Stage primaryStage) {
        // This method is called when the JavaFX application is launched

        // Run later on the JavaFX Application Thread to avoid IllegalStateException
        Platform.runLater(() -> {
            MapFrame frame = new MapFrame();
            Stage mapStage = new Stage();
            try {
                frame.start(mapStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }



}
