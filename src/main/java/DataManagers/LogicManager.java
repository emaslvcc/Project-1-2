package DataManagers;

import Bus.BusConnection;
import Bus.DirectConnection;
import Calculators.AverageTimeCalculator;
import Calculators.TimeCalculator;
import GUI.createMap;
import com.graphhopper.GraphHopper;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.config.Profile;
import com.graphhopper.util.*;
import java.util.List;
import javax.swing.*;

public class LogicManager extends GetUserData {
    private static DataManagers.Graph graph = new DataManagers.Graph();
    protected int time;
    protected double distance;
    DirectConnection directConnection = new DirectConnection();
    protected int [] finalStops;

    private int range = 500;

    /**
     * This method takes care of the main logic regarding the post codes.
     *
     * @param startCodeField Start Post Code.
     * @param endCodeField   End Post Code.
     * @param modeBox        Option of walking or cycling.
     */
    public void calculateLogic(JTextField startCodeField, JTextField endCodeField, JComboBox<String> modeBox) {
        BusConnection con;
        dataBaseReader.createHashMap();
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

        if ((mode).equals("Bus")){
            con = directConnection.bestWay(startPostCode, endPostCode, range);
            time = con.getTravelTime()/60;
            //TODO HERE SHOULD BE LINE FOR DRAWING LINE BASED ON BUS STOPS
            //createMap.drawPath(con.getRouteNodes());
        } else {
            GUI.createMap.updateCoord(startPostCode, endPostCode);
            calculateRoute(startPostCode, endPostCode, mode);
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

    public static void createGraph() {

        System.out.println("Creating graph...");
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

        com.graphhopper.storage.Graph graphHopper = hopper.getBaseGraph();
        NodeAccess nodeAccess = graphHopper.getNodeAccess();

        // iterate over every node and add it to the graph
        for (int i = 0; i < graphHopper.getNodes(); i++) {
            double lat = nodeAccess.getLat(i);
            double lon = nodeAccess.getLon(i);
            graph.addNode(new Node(i, lat, lon));
        }

        // iterate over every edge and add it to the graph
        //edgeiterator is a built-in class that allows us to iterate over all the edges in the graph
        EdgeIterator edgeIterator = graphHopper.getAllEdges();
        while (edgeIterator.next()) {
            int edgeId = edgeIterator.getEdge();
            int baseNode = edgeIterator.getBaseNode();
            int adjNode = edgeIterator.getAdjNode();
            double distance = edgeIterator.getDistance();
            graph.addEdge(new Edge(edgeId, graph.nodes.get(baseNode), graph.nodes.get(adjNode), distance));
        }
        System.out.println("Graph created");
    }

    public void calculateRoute(PostCode startPostCode, PostCode endPostCode, String mode) {
        try {// Find the start and end nodes
            Node startNode = graph.getNodeByLatLon(startPostCode.getLatitude(), startPostCode.getLongitude());
            Node endNode = graph.getNodeByLatLon(endPostCode.getLatitude(), endPostCode.getLongitude());

            // Create an AStar object
            Calculators.AStar aStar = new Calculators.AStar(graph);

            // Find the shortest path
            List<Node> shortestPath = aStar.findShortestPath(startNode, endNode);
            distance = calculateDistance(shortestPath);
            // Display the shortest path on the map
            GUI.createMap.drawPath(shortestPath);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public double calculateDistance(List<Node> path) {
        double distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node startNode = path.get(i);
            Node endNode = path.get(i + 1);
            distance += Calculators.DistanceCalculatorHaversine.calculate(startNode.getLon(), startNode.getLat(), endNode.getLon(), endNode.getLat());
        }

        return Double.parseDouble(String.format("%.2f", distance));
    }


}
