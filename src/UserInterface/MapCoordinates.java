package UserInterface;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class is responsible for calculating the coordinates to draw.
 */
public class MapCoordinates {

    public double x;
    public double y;
    public Circle circle;

    /**
     * Creating circle object to plot on map.
     *
     * @param latitude latitude coordinate
     * @param longitude longitude coordinate
     *
     */
    public MapCoordinates(double latitude, double longitude) {
        this.x = getX(longitude);
        this.y = getY(latitude);
        this.circle = getCircle();
    }

    /**
     * Defines the circle's style.
     *
     * @return Circle object with correct x and y-coordinates
     */
    public Circle getCircle() {
        Circle circle = new Circle(3, Color.RED);
        circle.setTranslateX(x);
        circle.setTranslateY(y);
        return circle;
    }

    /**
     * Convert real world longitude into x-coordinate to plot a circle.
     *
     * @param realLongitude real world latitude.
     *
     * @return x-coordinate to plot circle.
     */
    public double getX(double realLongitude) {
        return -606.5 + (realLongitude -  5.487500) * (1982/0.414722222222222);
    }

    /**
     * Convert real world latitude into y-coordinate to plot a circle.
     *
     * @param realLatitude real world latitude.
     *
     * @return y-coordinate to plot circle.
     */
    public double getY(double realLatitude) {
        return -428 + (realLatitude - 50.93833333333333) * (1284/-0.17472222222222);
    }
}