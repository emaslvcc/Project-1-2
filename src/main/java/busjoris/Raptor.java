package busjoris;

import Bus.BusConnectionDev;
import Calculators.AStar;
import Calculators.AverageTimeCalculator;
import Calculators.DistanceCalculatorHaversine;
import DataManagers.Graph;
import DataManagers.Node;
import Database.DatabaseConnection;
import GUI.createMap;
import GUI.mapFrame;
import GUI.transferModule;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Raptor {
    String startStation = "";
    List<StopTime> startingStations = new ArrayList<>();
    String endStation ="";
    List<StopTime> endingStations = new ArrayList<>();
    int nodeCount = 0;
    Graph graph;
    Stack<transferModule> transferInfoStack = new Stack<>();
    LocalTime endTime;
    double totalDist = 0;

    public Raptor(Graph g){
        this.graph = g;
    }

    public List<StopTime> setupNearestStops(Connection conn, double lat, double lon, LocalTime startTime, double dist) throws SQLException {
        List<StopTime> nearbyStops = new ArrayList<>();
        String query = """
SELECT
    stop_id,
    stop_lat,
    stop_lon
FROM
    stops
WHERE
    ST_Distance_Sphere(
        POINT(stop_lon, stop_lat),
        POINT(?, ?)
    ) <= ?;
""";

        try (PreparedStatement pstmt1 = conn.prepareStatement(query)){
            pstmt1.setDouble(1, lon);
            pstmt1.setDouble(2, lat);
            pstmt1.setDouble(3, dist);

            ResultSet res = pstmt1.executeQuery();
            while(res.next()){
                String stopId = res.getString(1);
                double stopLat = res.getDouble(2);
                double stopLon = res.getDouble(3);

                List<Node> walkingNodes = getAndDrawWalking(lat, lon, stopLat, stopLon, conn);
                double distance = getDistance(walkingNodes);
                double minsToWalk = getWalkingTime(distance);

                nearbyStops.add(new StopTime(stopId, startTime.plusMinutes((long) minsToWalk), startTime));
            }
        }
        return nearbyStops;
    }




    public LocalTime dino(double startLat, double startLon, double endLat, double endLon, LocalTime startTime) throws SQLException {                         // using this: https://www.microsoft.com/en-us/research/wp-content/uploads/2012/01/raptor_alenex.pdf as a guide
        Connection conn;
        try {
            conn = DatabaseConnection.getConnection();
            endingStations.addAll(setupNearestStops(conn, endLat, endLon, startTime, 500));
            startingStations.addAll(setupNearestStops(conn, startLat, startLon, startTime, 500));

//            System.out.println("starting stations");
//            for(StopTime lol: startingStations){
//                System.out.println(lol.getStopID() + "  "+ lol.getTime());
//            }
//            System.out.println();
//
//            System.out.println("ending stations");
//            for(StopTime lol: endingStations){
//                System.out.println(lol.getStopID() + "  "+ lol.getTime());
//            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Map<String, String[]> routeTracker = new HashMap<>();

        Set<String> checkedTripIDs = new HashSet<>();

        String startNode = startStation;
        String endNode = endStation;
        int transfers = 0;
        Map<String, DepartureAndArrival> stopArrivalTime = new HashMap<>();

        //Set<String> visitedTrips = new HashSet<>();
        Queue<Transfer> transferToCheck = new LinkedList<>();

        Queue<StopTime> busStopsToCheckForTrips = new LinkedList<>();

        for(StopTime stop : startingStations){
            busStopsToCheckForTrips.add(stop);   // aka mark Ps
            stopArrivalTime.put(stop.getStopID(), new DepartureAndArrival(stop.getStartTime(), stop.getTime()));
        }




        routeTracker.put(startNode, new String[]{"","",""});
        while(transfers < 2){
            //System.out.println("transfer:   "+ transfers);

            // This first block is just for getting all the trips based on the stops that we accumulated
            while(!busStopsToCheckForTrips.isEmpty()){
                StopTime stop = busStopsToCheckForTrips.poll();
                List<String> routes = getRoutes(conn, stop.getStopID());   // add stuff
                for (String route : routes){                            // for each route served by stop
                    Transfer tr = getNextTripID(conn, route, stop.getStopID(), stop.getTime());    // here we get the next trip based on a bus station.
                    if(tr == null){
                        continue;
                    }
                    if(!checkedTripIDs.contains(tr.getTripID())){
                        transferToCheck.add(tr);
                        checkedTripIDs.add(tr.getTripID());
                    }
                }
            }

            while(!transferToCheck.isEmpty()){
                Transfer currentTransfer = transferToCheck.poll();
                //System.out.println("Checking stop "+ currentTransfer.getStopID() + "   with  tripId "+ currentTransfer.getTripID());
                LocalTime departedTime = getDepartedTime(conn, currentTransfer.getStopID(), currentTransfer.getTripID());


                String query = """
                SELECT st.stop_id, st.arrival_time
                FROM stop_times st
                WHERE st.trip_id = ?
                AND st.stop_sequence > (
                    SELECT st2.stop_sequence
                    FROM stop_times st2
                    WHERE st2.trip_id = st.trip_id
                    AND st2.stop_id = ?
                    );
                """;

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, currentTransfer.getTripID());
                    pstmt.setString(2, currentTransfer.getStopID());

                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        String stopId = rs.getString("stop_id");
                        String timeString = rs.getString("arrival_time");

                        String[] parts = timeString.split(":");
                        int hours = Integer.parseInt(parts[0]);
                        int minutes = Integer.parseInt(parts[1]);
                        int seconds = Integer.parseInt(parts[2]);

                        // Adjust hours if they are 24 or more
                        if (hours >= 24) {
                            hours = hours % 24;
                        }

                        // Reconstruct the time string with adjusted hours
                        String adjustedTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        // Convert the adjusted time string to LocalTime
                        LocalTime time =  LocalTime.parse(adjustedTimeString);



                        LocalTime currentArrivalTime = stopArrivalTime.getOrDefault(stopId, new DepartureAndArrival(LocalTime.MAX, LocalTime.MAX)).getArrival();
                        // the second and checks that the times are not further than 10 hours apart which should prevent rollover
                        if(!stopArrivalTime.containsKey(stopId) || (time.isBefore(currentArrivalTime) && Math.abs(currentArrivalTime.minusHours(time.getHour()).toSecondOfDay())  <  LocalTime.of(6,0).toSecondOfDay())){     // if our new found time is earlier or we have not visited this one before then update
                            stopArrivalTime.put(stopId, new DepartureAndArrival(departedTime, time));        // departed time is the time where the bus departed from the stop from before

                            busStopsToCheckForTrips.add(new StopTime(stopId, time));   // Add the bus stop to be checked in the next loop
                            routeTracker.put(stopId, new String[]{currentTransfer.getStopID(),currentTransfer.getBusInfo(), currentTransfer.getTripID()});
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //visitedTrips.add(currentTransfer.getTripID());
            }
            transfers++;
        }


//        System.out.println(stopArrivalTime.entrySet().stream()
//                .map(entry -> String.format("%-15s : %s", entry.getKey(), entry.getValue()))
//                .collect(Collectors.joining("\n")));

        endStation = fastestEnd(stopArrivalTime, endingStations, endLat, endLon,conn);

//        for(StopTime station: endingStations){
//            if(stopArrivalTime.containsKey(station.getStopID())){
//                System.out.println(stopArrivalTime.get(station.getStopID()).getArrival());
//            }
//        }


        String answer = endStation;


        List<Node> nodeList = new ArrayList<>();
        List<Node> stopList = new ArrayList<>();
        List<List<Node>> shapeList = new ArrayList<>();
        List<String> colourList = new ArrayList<>();
        while(routeTracker.containsKey(answer)){
            LocalTime departed = stopArrivalTime.get(answer).getDep();
            LocalTime arrived = stopArrivalTime.get(answer).getArrival();
            String buss =  routeTracker.get(answer)[1];
            String endStation = getStopName(conn,answer);
            String endStationId = answer;
            String tripID = routeTracker.get(answer)[2];

            System.out.println("Arriving at stop: " + endStation+   " at "+ arrived +"  "+"Departed at: " + departed + " taking bus: "+buss+"  tripID: "+tripID+"  --> \n");

            answer = routeTracker.get(answer)[0];
            String startStation = getStopName(conn,answer);
            String startStationID = answer;

            nodeList = queryShapeDetails(conn, tripID, startStationID, endStationId);
            shapeList.add(nodeList);
            colourList.add(getColor(tripID));
            totalDist += getDistance(nodeList);
            stopList.addAll(getBusStopsBetween(conn, startStationID, endStationId, tripID));

            transferInfoStack.add(new transferModule("Bus",departed.toString(), arrived.toString(), buss, startStation,endStation));
            //transferModule.addTransferModule("Bus",departed.toString(), arrived.toString(), buss, startStation,endStation);
        }
        createMap.drawPath(shapeList, stopList, colourList);   //getColor(routeTracker.get(oldAnswer)[2]));

        transferInfoStack.add(new transferModule("Walking", stopArrivalTime.get(answer).getDep().toString(), stopArrivalTime.get(answer).getArrival().toString()));

        System.out.println("Arriving at stop: " + getStopName(conn,answer)+   " at "+ stopArrivalTime.get(answer).getArrival() +"  "+"Departed at: " + stopArrivalTime.get(answer).getDep() + " --> \n");


        System.out.println("Taking the Bus from station: "+ getStopName(conn,startStation)+ " to: " + getStopName(conn, endStation));



        //double[] latlon = getStationCoordinates(conn,endStation);

//        List<Node> walkPath = getAndDrawWalking(endLat, endLon, latlon[0], latlon[1], conn);
//        System.out.println(endLat + " "+ endLon + " "+ latlon[0]+ " "+ latlon[1]);
//        double distance = getDistance(walkPath);
//        double time = getWalkingTime(distance);

        //System.out.println("Walking to destination for "+ time +" minutes");
        while(!transferInfoStack.isEmpty()){
            transferModule.getTransfers().add(transferInfoStack.pop());
        }
        // setup time and distance in gui

        mapFrame.updateTimeField(Duration.between(startTime, endTime).toMinutesPart());
        int timediff;
        if(endTime.isBefore(startTime)){     // if there has been a overflow
            timediff = LocalTime.MAX.toSecondOfDay() - startTime.toSecondOfDay();
            timediff += endTime.toSecondOfDay();
        }else {
            timediff = endTime.toSecondOfDay() - startTime.toSecondOfDay();
        }
        timediff /= 60;

        mapFrame.updateDistanceField(totalDist);
        mapFrame.updateTimeField(timediff);

        return endTime;
    }
    // while less reps than the set max of transfers maybe smth like 2
    // for each next trip coming of from the start node
    // update the arrival time for each stop if  the arrival time is smaller than the one currently saved
    // also add the next trips you can take from there if they are not already in our list of completed trips


    public String fastestEnd(Map<String, DepartureAndArrival> times, List<StopTime> options, double lat, double lon, Connection conn) {
        String fastest = "";
        double shortestDist = 0;
        LocalTime startTime = LocalTime.NOON;
        LocalTime endTime = LocalTime.NOON;
        LocalTime fastestTime = LocalTime.MAX;

        for(StopTime stop : options) {
            double[] latlon = getStationCoordinates(conn, stop.getStopID());
            List<Node> nodes = getAndDrawWalking(lat, lon, latlon[0], latlon[1], conn);

            double distance = getDistance(nodes);
            int minsToWalk = (int) getWalkingTime(distance);

            if (times.containsKey(stop.getStopID())) {
                if (times.get(stop.getStopID()).getArrival().plusMinutes(minsToWalk).isBefore(fastestTime)) {
                    fastest = stop.getStopID();
                    fastestTime = times.getOrDefault(stop.getStopID(), new DepartureAndArrival(LocalTime.MAX, LocalTime.MAX)).getArrival().plusMinutes(minsToWalk);
                    endTime = fastestTime;
                    startTime = times.getOrDefault(stop.getStopID(), new DepartureAndArrival(LocalTime.MAX, LocalTime.MAX)).getArrival();
                }
            }
        }

        this.endTime = endTime;
        transferInfoStack.add(new transferModule("Walk",startTime.toString(), endTime.toString()));
        totalDist += shortestDist;

        return fastest;
    }

    public List<Node> getAndDrawWalking(double lat1, double lon1, double lat2, double lon2, Connection conn){

        // Find the start and end nodes
        Node startNode = graph.getNodeByLatLon(lat1, lon1);
        Node endNode = graph.getNodeByLatLon(lat2, lon2);

        // Find the shortest path
        AStar aStar = new AStar(graph);


        // todo draw the walking
        return aStar.findShortestPath(startNode, endNode);
    }

    public double getDistance(List<Node> nodes){
        if(nodes == null){
            return 0;
        }
        return BusConnectionDev.calculateTotalDistance(nodes);
    }

    public double getWalkingTime(double distance){
        AverageTimeCalculator time = new AverageTimeCalculator(distance);
        return time.getWalkingTime();
    }

    public LocalTime getDepartedTime(Connection conn, String stop, String trip) {
        String queryToGetDepartedTime = """
                SELECT st.departure_time
                FROM stop_times st
                WHERE st.trip_id = ?
                AND st.stop_id = ?
                """;


        try (PreparedStatement pstmt = conn.prepareStatement(queryToGetDepartedTime)) {
            pstmt.setString(1, trip);
            pstmt.setString(2, stop);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String timeString = rs.getString("departure_time");
                String[] parts = timeString.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);

                // Adjust hours if they are 24 or more
                if (hours >= 24) {
                    hours = hours % 24;
                }

                // Reconstruct the time string with adjusted hours
                String adjustedTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                // Convert the adjusted time string to LocalTime
                return LocalTime.parse(adjustedTimeString);

            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


        public List<String> getRoutes(Connection conn, String stop){

        String query = """
SELECT DISTINCT
    r.route_id
FROM
    stop_times st
JOIN
    trips t ON st.trip_id = t.trip_id
JOIN
    routes r ON t.route_id = r.route_id
WHERE
    st.stop_id = ?;
""";

        List<String> routes = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stop);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                routes.add(rs.getString("route_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return routes;
    }

    public String routeName(Connection conn, String tripID){
        String query = """
SELECT
    r.route_short_name,
    r.route_long_name
FROM
    trips t
JOIN
    routes r ON t.route_id = r.route_id
WHERE
    t.trip_id = ?;
""";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tripID);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getString(1) + "  " + rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getStopName(Connection conn, String stopID){
        String query = """
SELECT
    s.stop_name
FROM
    stops s
WHERE
    s.stop_id = ?
    ;
""";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stopID);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public Transfer getNextTripID(Connection conn, String route, String stopID, LocalTime transferTime){
        transferTime = transferTime.plusMinutes(1);
        String tripId = "";
        String query = """
SELECT
    t.trip_id,
    st.departure_time
FROM
    trips t
JOIN
    stop_times st ON t.trip_id = st.trip_id
WHERE
    t.route_id = ?
    AND st.departure_time >= ?
    AND st.stop_id = ?
ORDER BY
    st.departure_time
LIMIT 1;
""";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, route);
            pstmt.setTime(2, Time.valueOf(transferTime));
            pstmt.setString(3, stopID);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                tripId = rs.getString(1);
            }
            if(tripId.isEmpty() && transferTime.isAfter(LocalTime.of(23,0))){  // When we are not able to find a trip later in the day get the earliest one
                String query2 = """
SELECT
    t.trip_id,
    st.departure_time
FROM
    trips t
JOIN
    stop_times st ON t.trip_id = st.trip_id
WHERE
    t.route_id = ?
    AND st.stop_id = ?
ORDER BY
    st.departure_time ASC
LIMIT 1;
""";
                try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
                    pstmt2.setString(1, route);
                    pstmt2.setString(2, stopID);

                    ResultSet rs2 = pstmt2.executeQuery();
                    if (rs2.next()) {
                        tripId = rs2.getString(1);
                    }
                    if(tripId.isEmpty()){
                        return null;
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String businfo = routeName(conn, tripId);
        return new Transfer(stopID, tripId, businfo);
    }


    public double[] getStationCoordinates(Connection conn, String stopID){
        double[] latlon = new double[2];
        String query = """
            SELECT stop_lat, stop_lon
            from stops
            where stop_id = ?;
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stopID);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                latlon[0] = rs.getDouble(1);
                latlon[1] = rs.getDouble(2);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return latlon;
    }

    public List<Node> getBusStopsBetween(Connection conn, String startId, String endID, String tripID){
        List<Node> stopNodes = new ArrayList<>();

        String query = """
SELECT
    s.stop_lat,
    s.stop_lon
FROM
    stop_times st
JOIN
    stops s ON st.stop_id = s.stop_id
WHERE
    st.trip_id = ?
    AND st.stop_sequence BETWEEN
        (SELECT st1.stop_sequence FROM stop_times st1 WHERE st1.trip_id = st.trip_id AND st1.stop_id = ?)
        AND
        (SELECT st2.stop_sequence FROM stop_times st2 WHERE st2.trip_id = st.trip_id AND st2.stop_id = ?)
ORDER BY
    st.stop_sequence;
""";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tripID);
            pstmt.setString(2, startId);
            pstmt.setString(3, endID);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                double lat = rs.getDouble("stop_lat");
                double lon = rs.getDouble("stop_lon");
                stopNodes.add(new Node(0,lat,lon));
            }

        }catch (Exception e){
            System.out.println("whopsi");
        }

        return stopNodes;
    }





    /**
     * Queries and prints shape details for a given trip, start stop, and end stop.
     *
     * @param conn    the database connection
     * @param tripId  the trip ID
     * @param startId the start stop ID
     * @param endId   the end stop ID
     * @throws SQLException if a database error occurs
     */
    private List<Node> queryShapeDetails(Connection conn, String tripId, String startId, String endId) throws SQLException {
        List<Node> tripNodes = new ArrayList<>();
        // First, determine the latitude and longitude of the start and end stops
        double startLat = 0, startLon = 0, endLat = 0, endLon = 0;
        double[] startLatLon;
        double[] endLatLon;

        startLatLon = getStationCoordinates(conn,startId);
        startLat = startLatLon[0];
        startLon = startLatLon[1];

        endLatLon = getStationCoordinates(conn,endId);
        endLat = endLatLon[0];
        endLon = endLatLon[1];


        // Then, find the nearest shape points to these coordinates and the sequences
        String shapeSeqQuery = """
                SELECT s.shape_pt_sequence
                FROM shapes s
                JOIN trips t ON s.shape_id = t.shape_id
                WHERE t.trip_id = ?
                ORDER BY ST_Distance_Sphere(point(s.shape_pt_lon, s.shape_pt_lat), point(?, ?))
                LIMIT 1;
                """;

        int startShapeSeq = 0, endShapeSeq = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(shapeSeqQuery)) {
            pstmt.setString(1, tripId);
            pstmt.setDouble(2, startLon);
            pstmt.setDouble(3, startLat);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                startShapeSeq = rs.getInt("shape_pt_sequence");
            }

            pstmt.setDouble(2, endLon);
            pstmt.setDouble(3, endLat);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                endShapeSeq = rs.getInt("shape_pt_sequence");
            }
        }

        // Retrieve all shape points between the determined sequences
        String finalQuery = """
                SELECT s.shape_id, s.shape_pt_sequence, s.shape_pt_lat, s.shape_pt_lon
                FROM shapes s
                JOIN trips t ON s.shape_id = t.shape_id
                WHERE t.trip_id = ? AND s.shape_pt_sequence BETWEEN ? AND ?
                ORDER BY s.shape_pt_sequence;
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(finalQuery)) {
            pstmt.setString(1, tripId);
            pstmt.setInt(2, Math.min(startShapeSeq, endShapeSeq)); // Ensure correct order
            pstmt.setInt(3, Math.max(startShapeSeq, endShapeSeq));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String shapeId = rs.getString("shape_id");
                int shapePtSequence = rs.getInt("shape_pt_sequence");
                double shapePtLat = rs.getDouble("shape_pt_lat");
                double shapePtLon = rs.getDouble("shape_pt_lon");

                //System.out.println("Shape ID: " + shapeId +
                //", Shape Pt Sequence: " + shapePtSequence +
                //", Latitude: " + shapePtLat +
                //", Longitude: " + shapePtLon);


                tripNodes.add(new Node(nodeCount, shapePtLat, shapePtLon));
                nodeCount++;
            }
        }
        return tripNodes;
    }

    public String getColor(String tripID) throws SQLException {
        String query = """
        SELECT
            r.route_color
        FROM
            trips t
        JOIN
            routes r ON t.route_id = r.route_id
        WHERE
            t.trip_id = ?;
""";


        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, tripID);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString(1);
        }
        return "blue";

    }

}
