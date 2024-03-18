
package DataManagers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import Calculators.DistanceCalculator;

import java.util.HashMap;
import java.util.Map;

public class DataBaseReader {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Map<String, double[]> dataMap = new HashMap<>();

        String path = "src/DataManagers/MassZipLatLon.csv";

        int a = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
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

        // Input
        // String startZipCode = "6211AL";
        // String endZipCode = "6216EG";

        System.out.println("Enter your start zip code: ");
        String startZipCode = scanner.nextLine();

        System.out.println("Enter your end zip code: ");
        String endZipCode = scanner.nextLine();

        scanner.close();

        if (dataMap.containsKey(startZipCode) && dataMap.containsKey(endZipCode)) {
            double startlat = dataMap.get(startZipCode)[0];
            double startlon = dataMap.get(startZipCode)[1];

            double endlat = dataMap.get(endZipCode)[0];
            double endlon = dataMap.get(endZipCode)[1];

            double distance = DistanceCalculator.distanceCalculator(startlat, startlon, endlat, endlon);

            System.out.println("The distance is: " + String.format("%.3f", distance) + " kilometers.");
        } else {
            System.out.println("Not found");
        }

        /*    */

    }
}
