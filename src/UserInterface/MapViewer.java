package UserInterface;

import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class MapViewer {
    private double dragStartX;
    private double dragStartY;
    final private int minZoom = 1;
    final private int maxZoom = 7;
    public Pane pointsPane;
    public Pane linesPane;
    private ImageView mapView;

    public SubScene createMapSubScene(int width, int height) {
        // Load the image
        Image mapImage = new Image("/images/mapBig.png");

        // Create the ImageView
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(false);
        mapView.setCache(true);
        mapView.setFitWidth(2000);

        // Create a Pane to hold the points
        pointsPane = new Pane();

        // Create a Pane to hold the lines
        linesPane = new Pane();

        // Create a StackPane to add map and shapes to draw
        StackPane mapPane = new StackPane(mapView, linesPane, pointsPane);

        // Add zoom functionality
        mapPane.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double zoomFactor = 1.25;
            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }
            if ((mapPane.getScaleX() * zoomFactor) >= minZoom && (mapPane.getScaleX() * zoomFactor) <=maxZoom) {
                mapPane.setScaleX(mapPane.getScaleX() * zoomFactor);
                mapPane.setScaleY(mapPane.getScaleY() * zoomFactor);
            }
        });

        // Add panning functionality
        mapPane.setOnMousePressed(event -> {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });

        mapPane.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - dragStartX;
            double offsetY = event.getSceneY() - dragStartY;

            double newTranslateX = mapPane.getTranslateX() + offsetX;
            double newTranslateY = mapPane.getTranslateY() + offsetY;

            mapPane.setTranslateX(newTranslateX);
            mapPane.setTranslateY(newTranslateY);

            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });

        // Calculate is clicked:
        // Generate coordinates from postal codes is called
        MapCoordinates location1 = new MapCoordinates(50.85523285, 5.692237193);
        MapCoordinates location2 = new MapCoordinates(50.84760565, 5.669601806);
        pointsPane.getChildren().add(location1.circle);
        pointsPane.getChildren().add(location2.circle);

        Line line = new Line(location1.x, location1.y, location2.x, location2.y); // Adjust size and color as needed
        line.setStroke(Color.RED);
        linesPane.getChildren().add(line);

        return new SubScene(mapPane, width, height);
    }
}