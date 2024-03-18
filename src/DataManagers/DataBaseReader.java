
package DataManagers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import Calculators.DistanceCalculatorHaversine;
import Calculators.DistanceCalculatorPythagoras;

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

        System.out.println("Enter your start zip code: ");
        String startZipCode = scanner.nextLine();

        System.out.println("Enter your end zip code: ");
        String endZipCode = scanner.nextLine();

        scanner.close();

        if (dataMap.containsKey(startZipCode) && dataMap.containsKey(endZipCode)) {
            PostCode startPostCode = new PostCode(startZipCode, dataMap.get(startZipCode)[0], dataMap.get(startZipCode)[1]);
            PostCode endPostCode = new PostCode(endZipCode, dataMap.get(endZipCode)[0], dataMap.get(endZipCode)[1]);

            DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(startPostCode, endPostCode);
            DistanceCalculatorPythagoras calc2 = new DistanceCalculatorPythagoras(startPostCode, endPostCode);

            System.out.println("The distance with the Haversine formula is: " + String.format("%.3f", calc1.getDistance()) + " kilometers.");
            System.out.println("The distance with the Pythagorean formula is: " + String.format("%.3f", calc2.getDistance()) + " kilometers.");
        } else {
            System.out.println("Not found");
        }
    }
}
