package UserInterface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        System.out.println("Map of Maastricht!");
        // Launch the JavaFX application
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