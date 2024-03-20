package UserInterface;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class MapCoordinates {

    public double x;
    public double y;
    public Circle circle;

    public MapCoordinates(double latitude, double longitude) {
        this.x = getX(longitude);
        this.y = getY(latitude);
        this.circle = getCircle(x, y);
    }

    public Circle getCircle(double latitude, double longitude) {
        Circle circle = new Circle(3, Color.RED);
        circle.setTranslateX(x);
        circle.setTranslateY(y);
        return circle;
    }

    public double getX(double realLongitude) {
        double longitudeRatio = (10 - 15) / (5.697302 - 5.698019);
        return 406 + (realLongitude - 5.697302) * longitudeRatio;
    }

    public double getY(double realLatitude) {
        double latitudeRatio = (50 - 10) / (50.846210 - 50.851855);
        return 248 + (realLatitude - 50.846210) * latitudeRatio;
    }
}
