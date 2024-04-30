package DataManagers;

import Calculators.AverageTimeCalculator;
import Calculators.TimeCalculator;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
import com.graphhopper.config.Profile;
import com.graphhopper.util.*;

import java.util.Locale;
import javax.swing.*;

public class LogicManager extends GetUserData {

    protected int time;
    protected double distance;

    /**
     * This method takes care of the main logic regarding the post codes.
     *
     * @param startCodeField Start Post Code.
     * @param endCodeField   End Post Code.
     * @param modeBox        Option of walking or cycling.
     */
    public void calculateLogic(JTextField startCodeField, JTextField endCodeField, JComboBox<String> modeBox) {
        createHashMap();
        try {
            startPostCode = getStartZip(startCodeField);
            Thread.sleep(5000);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try {
            endPostCode = getEndZip(endCodeField);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        String mode = modeBox.getSelectedItem().toString();

        GUI.createMap.updateCoord(startPostCode, endPostCode);
        calculateRoute(startPostCode, endPostCode, mode);

        distance = Math.round(calculateAfterPressedButton(startPostCode, endPostCode) * 100d) / 100d;

        TimeCalculator timeCalc = new AverageTimeCalculator(distance);

        if ((mode).equals("Walk")) {
            time = (int) (Math.round(timeCalc.getWalkingTime()));
        } else if ((mode).equals("Bike")) {
            time = (int) (Math.round(timeCalc.getCyclingTime()));
        }
    }

    public void calculateRoute(PostCode startPostCode, PostCode endPostCode, String mode) {
        try {
            GraphHopper hopper = new GraphHopper();

            // Set the OSM file and the location of the graph cache
            hopper.setOSMFile("src/main/resources/Map/Maastricht.osm.pbf");
            hopper.setGraphHopperLocation("graph-cache");


            hopper.setEncodedValuesString("foot_access, foot_average_speed, bike_access, bike_average_speed, hike_rating, foot_priority, bike_priority, roundabout");

            // Define the walking and biking profiles with custom models
            hopper.setProfiles(
                    new Profile("walk").setCustomModel(GHUtility.loadCustomModelFromJar("foot.json")),
                    new Profile("bike").setCustomModel(GHUtility.loadCustomModelFromJar("bike.json"))
            );

            // Import the OSM file and load the graph
            hopper.importOrLoad();

            GHRequest req = new GHRequest(startPostCode.getLatitude(), startPostCode.getLongitude(),
                    endPostCode.getLatitude(), endPostCode.getLongitude())
                    .setProfile(mode.toLowerCase());

            GHResponse rsp = hopper.route(req);

            // Get the shortest path
            ResponsePath path = rsp.getBest();

            // Display the shortest path on the map
            GUI.createMap.drawPath(path);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
