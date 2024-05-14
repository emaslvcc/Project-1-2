
package DataManagers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The DataBaseReader class extends APICaller and provides functionality to read data from a CSV 
 * file and interact with a data map containing postal code coordinates.
 */
public class DataBaseReader extends APICaller {

    protected Map<String, double[]> dataMap = new HashMap<>();
    private final String PATH = "src/main/java/DataManagers/MassZipLatLon.csv";

    /**
     * Translates the CSV file to a HashMap containing postal codes and their coordinates.
     */
    private void csvToHashMap (){

        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            br.readLine(); // Skips header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Use a comma to separate entries
                double lat = Double.parseDouble(data[1]);
                double lon = Double.parseDouble(data[2]);
                dataMap.put(data[0], new double[] { lat, lon });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a HashMap from the CSV file.
     */
    public void createHashMap(){
        csvToHashMap();
    }

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
            updateCSVFile(zipCode, latitude, longitude);

            // Update the dataMap HashMap
            csvToHashMap();

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
    private void updateCSVFile(String zipCode, double latitude, double longitude) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, true))) {
            // Append the new postal code and coordinates to the CSV file
            writer.write( zipCode + "," + latitude + "," + longitude + "\n");
        }
    }

    /**
     * Extracts the latitude from the API response.
     * 
     * @param apiResponse The response from the API.
     * @return The latitude extracted from the response.
     */
    private double extractLatitude(String apiResponse) {

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
    private double extractLongitude(String apiResponse) {

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