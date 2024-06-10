
package DataManagers;

import Database.DatabaseConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * The DataBaseReader class extends APICaller and provides functionality to read data from a CSV 
 * file and interact with a data map containing postal code coordinates.
 */
public class DataBaseReader extends APICaller {

    /**
     * Saves a new postal code and its coordinates to the CSV file and updates the data map.
     * 
     * @param zipCode The postal code to be saved.
     */
    public void saveNewPostCode(String zipCode) {
        try {
            // Call the API to get coordinates for the given postal code
            String apiResponse = sendPostRequest(zipCode);

            // Parse the API response to extract latitude and longitude
            double latitude = extractLatitude(apiResponse);
            double longitude = extractLongitude(apiResponse);

            // Update the CSV file with the new postal code and coordinates
            updateDatabase(zipCode, latitude, longitude);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the CSV file with the new postal code and coordinates.
     * 
     * @param zipCode The postal code to save.
     * @param latitude The latitude of the postal code.
     * @param longitude The longitude of the postal code.
     * @throws IOException If an I/O error occurs.
     */
    public void updateDatabase(String zipCode, double latitude, double longitude) throws IOException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Corrected SQL query
        String query = "INSERT INTO post_codes (zipcode, latitude, longitude) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt1 = conn.prepareStatement(query)) {
            pstmt1.setString(1, zipCode);
            pstmt1.setDouble(2, latitude);
            pstmt1.setDouble(3, longitude);
            pstmt1.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Extracts the latitude from the API response.
     * 
     * @param apiResponse The response from the API.
     * @return The latitude extracted from the response.
     */
    public double extractLatitude(String apiResponse) {

        String latitude;
        double finalLatitude;

        int startIndex = apiResponse.indexOf("\"latitude\":") + "\"latitude\":".length() + 1;
        int endIndex = apiResponse.indexOf(",", startIndex);
        latitude = apiResponse.substring(startIndex, endIndex);
        latitude = latitude.replaceAll("\"", "");
        finalLatitude = Double.parseDouble(latitude);

        return finalLatitude;
    }

    /**
     * Extracts the longitude from the API response.
     * 
     * @param apiResponse The response from the API.
     * @return The longitude extracted from the response.
     */
    public double extractLongitude(String apiResponse) {

        String longitude;
        double finalLongitude;

        int startIndex = apiResponse.indexOf("\"longitude\":") + "\"longitude\":".length() + 1;
        int endIndex = apiResponse.indexOf(",", startIndex);
        longitude = apiResponse.substring(startIndex, endIndex);
        longitude = longitude.replaceAll("\"", "");
        finalLongitude = Double.parseDouble(longitude);

        return finalLongitude;
    }
}