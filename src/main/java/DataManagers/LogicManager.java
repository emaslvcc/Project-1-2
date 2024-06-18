package DataManagers;

import Bus.BusConnectionDev;
import Calculators.AverageTimeCalculator;
import Calculators.TimeCalculator;
import com.graphhopper.GraphHopper;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.config.Profile;
import com.graphhopper.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * Manages the logic for calculating routes and handling user inputs.
 */
public class LogicManager extends GetUserData {
    private static Graph graph = new Graph();
    public static int time;
    public static double distance;
    public static String[] busInfo;
    protected int[] finalStops;
    private static List<Node> shortestPath;

    /**
     * This method takes care of the main logic regarding the post codes.
     *
     * @param startCodeField Start Post Code.
     * @param endCodeField   End Post Code.
     * @param modeBox        Option of walking or cycling.
     * @throws Exception
     */
    public void calculateLogic(JTextField startCodeField, JTextField endCodeField, JComboBox<String> modeBox)
            throws Exception {

        try {
            startPostCode = getStartZip(startCodeField);
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

        if ((mode).equals("Bus")) {
            BusConnectionDev.busLogic(startPostCode.getLatitude(), startPostCode.getLongitude(),
                    endPostCode.getLatitude(), endPostCode.getLongitude());
            // GUI.mapFrame.setBusInfo(busInfo[0], busInfo[1], busInfo[2], busInfo[3],
            // busInfo[4], busInfo[5]);
        } else {
            calculateRoute(startPostCode, endPostCode, mode);
            // Display the shortest path on the map

            GUI.createMap.drawPath(shortestPath);
        }

        TimeCalculator timeCalc = new AverageTimeCalculator(distance);
        GUI.mapFrame.updateDistanceField(distance);

        if ((mode).equals("Walk")) {
            time = (int) (Math.round(timeCalc.getWalkingTime()));
        } else if ((mode).equals("Bike")) {
            time = (int) (Math.round(timeCalc.getCyclingTime()));
        }
        GUI.mapFrame.updateTimeField(time);
    }

    /**
     * Creates the graph for routing purposes.
     */
    public static void createGraph() {
        System.out.println("Creating graph...");
        GraphHopper hopper = new GraphHopper();

        // Set the OSM file and the location of the graph cache
        hopper.setOSMFile("src/main/resources/Map/Maastricht.osm.pbf");
        hopper.setGraphHopperLocation("graph-cache");

        hopper.setEncodedValuesString(
                "foot_access, foot_average_speed, bike_access, bike_average_speed, hike_rating, foot_priority, bike_priority, roundabout");

        // Define the walking and biking profiles with custom models
        hopper.setProfiles(
                new Profile("walk").setCustomModel(GHUtility.loadCustomModelFromJar("foot.json")),
                new Profile("bike").setCustomModel(GHUtility.loadCustomModelFromJar("bike.json")));

        // Import the OSM file and load the graph
        hopper.importOrLoad();

        com.graphhopper.storage.Graph graphHopper = hopper.getBaseGraph();
        NodeAccess nodeAccess = graphHopper.getNodeAccess();
        Map<Integer, Node> nodes = new HashMap<>();

        // Iterate over every node and add it to the graph
        for (int i = 0; i < graphHopper.getNodes(); i++) {
            double lat = nodeAccess.getLat(i);
            double lon = nodeAccess.getLon(i);
            Node node = new Node(i, lat, lon);
            graph.addNode(node);
            nodes.put(i, node);
        }

        // Iterate over every edge and add it to the graph
        // edgeiterator is a built-in class that allows us to iterate over all the edges
        // in the graph
        EdgeIterator edgeIterator = graphHopper.getAllEdges();
        while (edgeIterator.next()) {
            int edgeId = edgeIterator.getEdge();
            int baseNode = edgeIterator.getBaseNode();
            int adjNode = edgeIterator.getAdjNode();
            double distance = edgeIterator.getDistance();
            graph.addEdge(new Edge(edgeId, nodes.get(baseNode), nodes.get(adjNode), distance));
            graph.addEdge(new Edge(edgeId, nodes.get(adjNode), nodes.get(baseNode), distance));
        }
        System.out.println("Graph created");
    }

    /**
     * Calculates the route based on the start and end post codes.
     *
     * @param startPostCode The starting post code.
     * @param endPostCode   The ending post code.
     * @param mode          The mode of transportation.
     */
    public void calculateRoute(PostCode startPostCode, PostCode endPostCode, String mode) {
        try {
            // Find the start and end nodes
            Node startNode = graph.getNodeByLatLon(startPostCode.getLatitude(), startPostCode.getLongitude());
            Node endNode = graph.getNodeByLatLon(endPostCode.getLatitude(), endPostCode.getLongitude());

            // Find the shortest path
            Calculators.AStar aStar = new Calculators.AStar(graph);
            shortestPath = aStar.findShortestPath(startNode, endNode);
            distance = calculateDistance(shortestPath);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static List<Node> calculateRouteByCoordinates(double lat_1, double lon_1, double lat_2, double lon_2,
            String mode) {
        try {
            // Find the start and end nodes
            Node startNode = graph.getNodeByLatLon(lat_1, lon_1);
            Node endNode = graph.getNodeByLatLon(lat_2, lon_2);

            // Find the shortest path
            Calculators.AStar aStar = new Calculators.AStar(graph);
            shortestPath = aStar.findShortestPath(startNode, endNode);
            return shortestPath;
        } catch (Exception e) {
            System.out.println(e);
        }
        return shortestPath;
    }

    /**
     * Calculates the distance of a given path.
     *
     * @param path The path for which the distance is to be calculated.
     * @return The total distance of the path.
     */
    public static double calculateDistance(List<Node> path) {
        if (path == null) {
            return 0;
        }
        double distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node startNode = path.get(i);
            Node endNode = path.get(i + 1);
            distance += Calculators.DistanceCalculatorHaversine.calculate(startNode.getLat(), startNode.getLon(),
                    endNode.getLat(), endNode.getLon());
        }
        return distance;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

}
