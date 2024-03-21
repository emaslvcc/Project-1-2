package UserInterface;

import DataManagers.PostCode;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * This class is responsible for displaying the map, drawing points, drawing lines, panning and zooming.
 */
public class MapViewer {
    private double dragStartX;
    private double dragStartY;
    final private int minZoom = 1;
    final private int maxZoom = 7;
    public static Pane pointsPane;
    public static Pane linesPane;
    private ImageView mapView;

    private static double startX, startY, endX, endY;

    /**
     * This method creates a map subscene.
     *
     * @param width This sets the width size for the size of the map.
     *  @param height This sets the height size for the size of the map.
     *
     * @return This returns the map subscene that will be placed in the frame.
     */
    public SubScene createMapSubScene(int width, int height) {
        // Load the image
        Image mapImage = new Image("/Images/mapBig.png");

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
            if(deltaY == 0){
                return;
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

        return new SubScene(mapPane, width, height);
    }

    /**
     * Draws the points of two postcodes and a line connecting them on the map.
     */
    public static void drawLine(){

        // Generate coordinates from postal codes is called
        MapCoordinates location1 = new MapCoordinates(startX, startY);
        MapCoordinates location2 = new MapCoordinates(endX, endY);
        pointsPane.getChildren().clear();
        pointsPane.getChildren().add(location1.circle);
        pointsPane.getChildren().add(location2.circle);

        Line line = new Line(location1.x, location1.y, location2.x, location2.y); // Adjust size and color as needed
        line.setStroke(Color.RED);
        linesPane.getChildren().clear();
        linesPane.getChildren().add(line);
    }

    /**
     * Get the coordinates from input postcodes.
     *
     * @param startPostCode Starting postcode.
     * @param endPostCode Destination postcode.
     */
    public static void updateCord(PostCode startPostCode,PostCode endPostCode){
        startX = startPostCode.getLatitude();
        startY = startPostCode.getLongitude();
        endX = endPostCode.getLatitude();
        endY = endPostCode.getLongitude();
    }
}