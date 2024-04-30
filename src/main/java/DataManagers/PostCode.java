package DataManagers;

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
     * Retrieves the latitude.
     * @return The latitude.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Retrieves the longitude.
     * @return The longitude.
     */
    public double getLongitude() {
        return this.longitude;
    }


}