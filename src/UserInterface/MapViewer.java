package UserInterface;

import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;

public class MapViewer {
    private double dragStartX;
    private double dragStartY;
    final private int minZoom = 1;
    final private int maxZoom = 6;
    public SubScene createMapSubScene(int width, int height) {
        // Load the image
        Image mapImage = new Image("/images/mapBig.png");

        // Create the ImageView
        ImageView mapView = new ImageView(mapImage);
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
            if ((mapView.getScaleX() * zoomFactor) >= minZoom && (mapView.getScaleX() * zoomFactor) <=maxZoom) {
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

        // Create a StackPane to hold the ImageView
        StackPane root = new StackPane(mapView);

        // Create the SubScene
        SubScene subScene = new SubScene(root, width, height);

        return subScene;
    }
}
