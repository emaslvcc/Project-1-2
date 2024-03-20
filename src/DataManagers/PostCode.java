package DataManagers;

import java.util.Map;
import java.util.Scanner;

/**
 * The PostCode class represents a postal code with its corresponding latitude and longitude.
 */
public class PostCode {
    
    public String postCode;
    public double latitude;
    public double longitude;

    /**
     * Constructs a PostCode object with the specified postal code, latitude, and longitude.
     * 
     * @param postCode The postal code.
     * @param latitude The latitude.
     * @param longitude The longitude.
     */
    public PostCode(String postCode, double latitude, double longitude) {
        this.postCode = postCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Retrieves the postal code.
     * @return The postal code.
     */
    public String getPostCode() {
        return this.postCode;
    }

    /**
     * Sets the postal code.
     * @param postCode The postal code to set.
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * Retrieves the latitude.
     * @return The latitude.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Sets the latitude.
     * @param latitude The latitude to set.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Retrieves the longitude.
     * @return The longitude.
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Sets the longitude.
     * @param longitude The longitude to set.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}