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
    private Pane pointsPane;
    private Pane linesPane;
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

//        double mapWidth = 4493.8582677;
//        double mapHeight = 3178.5826772;
        double mapWidth = 14043;
        double mapHeight = 9933;

        double minLongitude = 5.4875;
        double maxLongitude = 5.9022;
        double minLatitude = 50.9383;
        double maxLatitude = 50.7636;

        double pointLongitude1 = 5.692237193;
        double pointLatitude1 = 50.85523285;

        double pointLongitude2 = 5.669601806;
        double pointLatitude2 = 50.84760565;

        double pointLongitude3 = 5.710954;
        double pointLatitude3 = 50.834869;

        double x1 = getX(pointLongitude1);
        double y1 = getY(pointLatitude1);
        double x2 = getX(pointLongitude2);
        double y2 = getY(pointLatitude2);
        double x3 = getX(pointLongitude3);
        double y3 = getY(pointLatitude3);

        addLine(x1,y1,x2,y2);
        addLine(x1,y1,x3,y3);

        addPoint(x1, y1);
        addPoint(x2, y2);
        addPoint(x3, y3);

        System.out.println(x1 + " " + y1);
        System.out.println(x2 + " " + y2);

        return new SubScene(mapPane, width, height);
    }

    private void addPoint(double x, double y) {
        Circle point = new Circle(3, Color.RED); // Adjust size and color as needed
        point.setTranslateX(x);
        point.setTranslateY(y);
        pointsPane.getChildren().add(point);
    }

    private void addLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY); // Adjust size and color as needed
        line.setStroke(Color.RED);
        linesPane.getChildren().add(line);
    }

    private double getX(double realLongitude) {
        double longitudeRatio = (10 - 15) / (5.697302 - 5.698019);
        double x = 406 + (realLongitude - 5.697302) * longitudeRatio;
        return x;
    }

    private double getY(double realLatitude) {
        double latitudeRatio = (50 - 10) / (50.846210 - 50.851855);
        double y = 248 + (realLatitude - 50.846210) * latitudeRatio;
        return y;
    }
}