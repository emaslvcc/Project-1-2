package UserInterface;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
        return -606.5 + (realLongitude -  5.487500) * (1982/0.414722);
    }

    public double getY(double realLatitude) {
        return -428 + (realLatitude - 50.938333) * (1284/-0.174722);
    }
}