package DataManagers;

import java.util.ArrayList;

/**
 * The PostCode class represents a postal code with its corresponding latitude and longitude.
 */
public class PostCode {

    public String postCode;
    private double latitude;
    private double longitude;
    private double score;
    private static ArrayList<Double> scores = new ArrayList<Double>();

    /**
     * Constructs a PostCode object with the specified postal code, latitude, and longitude.
     *
     * @param postCode  The postal code.
     * @param latitude  The latitude.
     * @param longitude The longitude.
     */
    public PostCode(String postCode, double latitude, double longitude) {
        this.postCode = postCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Retrieves the latitude.
     *
     * @return The latitude.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Retrieves the longitude.
     *
     * @return The longitude.
     */
    public double getLongitude() {
        return this.longitude;
    }

    public void setScore(double score) {
        this.score = score;
        scores.add(score);
    }

    public double getScore() {
        return this.score;
    }

    public static double getFirstThird() {
        return scores.get(scores.size() / 3);
    }

    public static double getSecondThird() {
        return scores.get(2 * (scores.size() / 3));
    }

    public static void sortScores(){
        scores.sort(null);
    }
}