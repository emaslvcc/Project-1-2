
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
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                // Use comma to separate entries
                String[] data = line.split(",");
                dataMap.put(data[0], new double[] { Double.parseDouble(data[1]), Double.parseDouble(data[2]) });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Input
        // String startZipCode = "6211AP";
        // String endZipCode = "6211JK";

        System.out.println("Enter your start zip code: ");
        String startZipCode = scanner.next();

        System.out.println("Enter your end zip code: ");
        String endZipCode = scanner.next();

        scanner.close();

        // 根据输入查找并打印第二和第三个值
        if (dataMap.containsKey(startZipCode) && dataMap.containsKey(endZipCode)) {
            double startlat = dataMap.get(startZipCode)[0];
            double startlon = dataMap.get(startZipCode)[1];

            double endlat = dataMap.get(endZipCode)[0];
            double endlon = dataMap.get(endZipCode)[1];

            double distance = DistanceCalculator.distanceCalculator(startlat, startlon, endlat, endlon);

            System.out.println("The distance is: " + distance + " kilometers.");
        } else {
            System.out.println("Not found");
        }

        /*    */

    }
}
