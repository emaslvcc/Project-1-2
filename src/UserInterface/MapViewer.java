package UserInterface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javafx.scene.shape.Line;
import java.util.Random;

public class MapViewer extends Application {

    private ImageView mapView;
    private double dragStartX;
    private double dragStartY;
    final private int minZoom = 1;
    final private int maxZoom = 3;

    @Override
    public void start(Stage primaryStage) {
        // Load the map image
        Image mapImage = new Image("/images/mapBig.png");

        // Create the ImageView for the map
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setCache(true);
        mapView.setFitWidth(2000);

        // Add zoom functionality
        mapView.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double zoomFactor = 1.1;
            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }
            if ((mapView.getScaleX() * zoomFactor) >= minZoom && (mapView.getScaleX() * zoomFactor) <= maxZoom) {
                mapView.setScaleX(mapView.getScaleX() * zoomFactor);
                mapView.setScaleY(mapView.getScaleY() * zoomFactor);
            }
        });

        // Add panning functionality
        mapView.setOnMousePressed(event -> {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });

        mapView.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - dragStartX;
            double offsetY = event.getSceneY() - dragStartY;

            double newTranslateX = mapView.getTranslateX() + offsetX;
            double newTranslateY = mapView.getTranslateY() + offsetY;

            mapView.setTranslateX(newTranslateX);
            mapView.setTranslateY(newTranslateY);

            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });

        // START OF DOTS CODE

        double mapWidth = 4493;
        double mapHeight = 3178;

        double minLongitude = 5.4875;
        double maxLongitude = 5.9022;
        double minLatitude = 50.7636;
        double maxLatitude = 50.9383;

        double pointLongitude1 = 5.692237193;
        double pointLatitude1 = 50.85523285;

        double pointLongitude2 = 5.669601806;
        double pointLatitude2 = 50.84760565;

        // Calculate scale factors to convert latitude and longitude differences to pixel differences
        double xScale = mapWidth / (maxLongitude - minLongitude);
        double yScale = mapHeight / (maxLatitude - minLatitude);

        // Convert real latitude and longitude to pixel coordinates
        double pixelX1 = (pointLongitude1 - minLongitude) * xScale;
        double pixelY1 = (maxLatitude - pointLatitude1) * yScale;
        double pixelX2 = (pointLongitude2 - minLongitude) * xScale;
        double pixelY2 = (maxLatitude - pointLatitude2) * yScale;

        Line line = new Line(pixelX1, pixelY1, pixelX2, pixelY2);
        line.setStroke(Color.RED); // Set the line color

        StackPane root = new StackPane(mapView, line);

        // END OF DOTS CODE

        // Create the scene
        Scene scene = new Scene(root, 500, 500);

        // Set up the stage
        primaryStage.setTitle("Map Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
