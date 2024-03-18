
package DataManagers;

import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataBaseReader extends APICaller{

    protected Map<String, double[]> dataMap = new HashMap<>();
    private final String PATH = "src/DataManagers/MassZipLatLon.csv";

    //translates csv file to a HashMap
    private void csvToHashMap (){

        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {

                // Use comma to separate entries
                String[] data = line.split(",");

                double lat = Double.parseDouble(data[1]);
                double lon = Double.parseDouble(data[2]);
                dataMap.put(data[0], new double[] { lat, lon });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNewPostCode(String zipCode){
        try {
            // Call the API to get coordinates for the given postal code
            String apiResponse = sendPostRequest(zipCode);

            // Parse the API response to extract latitude and longitude
            String latitude = extractLatitude(apiResponse);
            String longitude = extractLongitude(apiResponse);

            // Update the CSV file with the new postal code and coordinates
            updateCSVFile(zipCode, latitude, longitude);

            //update the dataMap HashMap
            csvToHashMap();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateCSVFile(String zipCode, String latitude, String longitude) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, true))) {
            // Append the new postal code and coordinates to the CSV file
            writer.write(zipCode + "," + latitude + "," + longitude + "\n");
        }
    }
    private String extractLatitude(String apiResponse) {

        String latitude;
        int startIndex = apiResponse.indexOf("\"latitude\":") + "\"latitude\":".length() + 1;
        int endIndex = apiResponse.indexOf(",", startIndex);

        latitude = apiResponse.substring(startIndex, endIndex);

        return latitude.trim();
    }

    private String extractLongitude(String apiResponse) {

        String longitude;
        int startIndex = apiResponse.indexOf("\"longitude\":") + "\"longitude\":".length() + 1;
        int endIndex = apiResponse.indexOf(",", startIndex);

        longitude = apiResponse.substring(startIndex, endIndex);

        return longitude.trim();
    }
}
