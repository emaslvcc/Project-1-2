
package DataManagers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataBaseReader {

    protected Map<String, double[]> dataMap = new HashMap<>();

    //translates csv file to a HashMap
    private void csvToHashMap (){

        String path = "src/DataManagers/MassZipLatLon.csv";

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
    }

    public void saveNewPostCode(String zipCode){

        csvToHashMap();
    }
}
