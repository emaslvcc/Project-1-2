package Bus;

import Calculators.AverageTimeCalculator;
import Calculators.DistanceCalculatorHaversine;
import DataManagers.Node;
import Database.DatabaseConnection;
import GUI.createMap;

import javax.xml.transform.Source;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class Raptor {
    String startStation = "";
    List<StopTime> startingStations = new ArrayList<>();
    String endStation ="";
    List<StopTime> endingStations = new ArrayList<>();
    int nodeCount = 0;

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

                double distance = DistanceCalculatorHaversine.calculate(lon, lat, stopLon,stopLat);                     // get distance from where we start to bus stop;
                AverageTimeCalculator time = new AverageTimeCalculator(distance);
                double minsToWalk = time.getWalkingTime();

                nearbyStops.add(new StopTime(stopId, startTime.plusMinutes((long) minsToWalk)));
            }
        }
        return nearbyStops;
    }




    public LocalTime dino(double startLat, double startLon, double endLat, double endLon,LocalTime startTime) throws SQLException {                         // using this: https://www.microsoft.com/en-us/research/wp-content/uploads/2012/01/raptor_alenex.pdf as a guide
        Connection conn;
        try {
            conn = DatabaseConnection.getConnection();
            endingStations.addAll(setupNearestStops(conn, endLat, endLon, startTime, 500));
            startingStations.addAll(setupNearestStops(conn, startLat, startLon, startTime, 500));

            System.out.println("starting stations");
            for(StopTime lol: startingStations){
                System.out.println(lol.getStopID() + "  "+ lol.getTime());
            }
            System.out.println();

            System.out.println("ending stations");
            for(StopTime lol: endingStations){
                System.out.println(lol.getStopID() + "  "+ lol.getTime());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Map<String, String[]> routeTracker = new HashMap<>();

        String startNode = startStation;
        String endNode = endStation;
        int transfers = 0;
        Map<String, DepartureAndArrival> stopArrivalTime = new HashMap<>();

        //Set<String> visitedTrips = new HashSet<>();
        Queue<Transfer> transferToCheck = new LinkedList<>();

        Queue<StopTime> busStopsToCheckForTrips = new LinkedList<>();

        for(StopTime stop : startingStations){
            busStopsToCheckForTrips.add(stop);   // aka mark Ps
            stopArrivalTime.put(stop.getStopID(), new DepartureAndArrival(stop.getTime(), stop.getTime()));
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
                    // todo add pruning do not check later trips it is just a waste of time
                        transferToCheck.add(tr);
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
                        LocalTime time = rs.getTime("arrival_time").toLocalTime();


                        if(stopArrivalTime.getOrDefault(stopId, new DepartureAndArrival(LocalTime.MAX, LocalTime.MAX)).getArrival().isAfter(time)){     // if our new found time is earlier or we have not visited this one before then update
                            stopArrivalTime.put(stopId, new DepartureAndArrival(departedTime, time));        // departed time is the time where the bus departed from the stop from before

                            busStopsToCheckForTrips.add(new StopTime(stopId, time));   // Add the bus stop to be checked in the next loop
//                            System.out.println("updated stopid     "+ stopId+ " time : "+ time);
//                            if(endingStations.contains(stopId)){
//                                System.out.println("lksad;lkdsaf;alkjdsf!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                            }
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

            String answer = endStation;

            StringBuilder builder = new StringBuilder();
            builder.insert(0,"Arriving at stop: " + getStopName(conn,answer)+   " at "+ stopArrivalTime.get(answer).getArrival() + " ");
            List<Node> nodeList = new ArrayList<>();
            List<Node> stopList = new ArrayList<>();
            while(routeTracker.containsKey(answer)){
                builder.insert(0,"Departing at: " + stopArrivalTime.get(answer).getDep() + " taking bus: "+routeTracker.get(answer)[1]+"  tripID: "+routeTracker.get(answer)[2]+"  --> \n");

                String oldAnswer = answer;

                answer = routeTracker.get(answer)[0];
                startStation = answer;
                builder.insert(0,"Arriving at stop: " + getStopName(conn,answer)+   " at "+ stopArrivalTime.get(answer).getArrival() +" ");

                // from answer to old answer
                nodeList.addAll(queryShapeDetails(conn, routeTracker.get(oldAnswer)[2],answer, oldAnswer));
                createMap.drawPath(queryShapeDetails(conn, routeTracker.get(oldAnswer)[2],answer, oldAnswer), getBusStopsBetween(conn,answer, oldAnswer, routeTracker.get(oldAnswer)[2]));
            }
        createMap.drawPath(nodeList, stopList);



        System.out.println("Taking the Bus from station: "+ getStopName(conn,startStation)+ " to: " + getStopName(conn, endStation));


        System.out.println(builder);

        double[] latlon = getStationCoordinates(conn,endStation);
        double distance = DistanceCalculatorHaversine.calculate(endLon, endLat, latlon[1],latlon[0]);                     // get distance from where we start to bus stop;
        AverageTimeCalculator time = new AverageTimeCalculator(distance);
        int minsToWalk = (int) time.getWalkingTime();

        System.out.println("Walking to destination for "+ minsToWalk +" minutes");

        return stopArrivalTime.get(endNode).arrival;
    }
        // while less reps than the set max of transfers maybe smth like 2
            // for each next trip coming of from the start node
                // update the arrival time for each stop if  the arrival time is smaller than the one currently saved
                // also add the next trips you can take from there if they are not already in our list of completed trips


    public String fastestEnd(Map<String, DepartureAndArrival> times, List<StopTime> options, double lat, double lon, Connection conn) {
        String fastest = "";
        LocalTime fastestTime = LocalTime.MAX;

        for(StopTime stop : options){
            double[] latlon = getStationCoordinates(conn, stop.getStopID());
            double distance = DistanceCalculatorHaversine.calculate(lon, lat, latlon[1],latlon[0]);                     // get distance from where we start to bus stop;
            AverageTimeCalculator time = new AverageTimeCalculator(distance);
            int minsToWalk = (int) time.getWalkingTime();

            if(times.containsKey(stop.getStopID())){
                if(times.get(stop.getStopID()).getArrival().plusMinutes(minsToWalk).isBefore(fastestTime)){
                    fastest = stop.getStopID();
                    fastestTime = times.getOrDefault(stop.getStopID(), new DepartureAndArrival(LocalTime.MAX, LocalTime.MAX)).getArrival().plusMinutes(minsToWalk);
                }
            }
        }
        return fastest;
    }


    public LocalTime getDepartedTime(Connection conn, String stop, String trip){
        String queryToGetDepartedTime = """
                SELECT st.arrival_time
                FROM stop_times st
                WHERE st.trip_id = ?
                AND st.stop_id = ?
                """;


        try (PreparedStatement pstmt = conn.prepareStatement(queryToGetDepartedTime)) {
            pstmt.setString(1, trip);
            pstmt.setString(2, stop);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getTime("arrival_time").toLocalTime();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
            if(tripId.isEmpty()){
                return null;
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

                System.out.println("Shape ID: " + shapeId +
                        ", Shape Pt Sequence: " + shapePtSequence +
                        ", Latitude: " + shapePtLat +
                        ", Longitude: " + shapePtLon);


                tripNodes.add(new Node(nodeCount, shapePtLat, shapePtLon));
                nodeCount++;
            }
        }
        return tripNodes;
    }


}