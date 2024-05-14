package DataManagers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The APICaller class provides functionality to send a POST request to an API
 * in order to retrieve the coordinates of a given postal code.
 */
public class APICaller {

    /**
     * Sends a POST request to the API to retrieve the coordinates of a given postal code.
     * 
     * @param postcode The postal code in Maastricht for which coordinates are requested.
     * @return The response from the API containing the coordinates.
     */

    static long latestUseTime = 0;

    protected static String sendPostRequest(String postcode) throws InterruptedException {

        String finalResponse = "";
        if (latestUseTime == 0) {
            latestUseTime = System.currentTimeMillis();
            System.out.println("Calling API.");
        } else if (System.currentTimeMillis() - latestUseTime < 5100) {
            System.out.println("Pausing for " + (System.currentTimeMillis() - latestUseTime) + " ms.");
            Thread.sleep(System.currentTimeMillis() - latestUseTime);
            latestUseTime = System.currentTimeMillis();
        }
        try {
            String urlString = "https://computerscience.dacs.unimaas.nl/get_coordinates?postcode=" + postcode;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"postcode\": \"" + postcode + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response);
                finalResponse = response.toString();

            } else {
                System.out.println("POST request didn't work.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalResponse;
    }
}