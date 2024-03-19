package UserInterface;

import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class MapViewer {
    private double dragStartX;
    private double dragStartY;
    final private int minZoom = 1;
    final private int maxZoom = 3;
    public SubScene createMapSubScene(int width, int height) {
        // Load the image
        Image mapImage = new Image("/images/mapBig.png");

        // Create the ImageView
        ImageView mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(false);
        mapView.setCache(true);
        mapView.setFitWidth(2000);

        // Create a StackPane to add map and shapes to draw
        StackPane mapPane = new StackPane(mapView);

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
        mapPane.getChildren().add(line);

        addPoint(100, 100, mapPane);

        return new SubScene(mapPane, width, height);
    }

    private void addPoint(double x, double y, StackPane pane) {
        Circle point = new Circle(5, Color.RED); // Adjust size and color as needed
        point.setTranslateX(x);
        point.setTranslateY(y);
        pane.getChildren().add(point);
    }
}
