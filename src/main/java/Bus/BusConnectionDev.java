package Bus;

import java.sql.*;

import java.util.*;

import Calculators.AverageTimeCalculator;
import Calculators.DistanceCalculatorHaversine;
import Calculators.TimeCalculator;
import DataManagers.LogicManager;
import DataManagers.Node;
import Database.DatabaseConnection;
import GUI.createMap;
import GUI.transferModule;

/**
 * Manages the logic behind bus trips.
 */
public class BusConnectionDev {

    public static boolean testClass = false;

    static List<Node> stopNodes = new ArrayList<>();
    static int number_stop_id = 0;

    static List<Node> tripNodes = new ArrayList<>();
    static int number_shape_id = 0;

    static TripInfo directBestTrip;
    static TripInfo transferBestTrip;
    static TripInfo bestTrip;
    static int time;

    static String busName = "";
    static String busNumber = "";
    static String startBusStop = "";
    static String endBusStop = "";
    static String departureTime = "";
    static String arrivalTime = "";

    static double aerialDistance = 0;
    static int directTripTime = 0;

    /**
     * Resets the lists and IDs used for tracking stops and trips.
     */
    public static void resetLists() {
        stopNodes = new ArrayList<>();
        number_stop_id = 0;
        tripNodes = new ArrayList<>();
        number_shape_id = 0;
    }

    /**
     * Calculates the total distance between a list of nodes using the Haversine
     * formula.
     * 
     * @param nodes the list of nodes
     * @return the total distance in kilometers
     */
    public static double calculateTotalDistance(List<Node> nodes) {
        double totalDistance = 0.0;
        for (int i = 0; i < nodes.size() - 1; i++) {
            totalDistance += DistanceCalculatorHaversine.haversineDistance(nodes.get(i), nodes.get(i + 1));
        }
        return totalDistance;
    }

    /**
     * Finds and processes the best bus route between two coordinates.
     * 
     * @param x1 the latitude of the start location
     * @param y1 the longitude of the start location
     * @param x2 the latitude of the end location
     * @param y2 the longitude of the end location
     * @throws Exception if no direct bus connection is found or if a database error
     *                   occurs
     */
    public static void busLogic(double x1, double y1, double x2, double y2) throws Exception {
        try {
            Connection conn = DatabaseConnection.getConnection();
            aerialDistance = DistanceCalculatorHaversine.calculate(x1, y1, x2, y2);

            if (aerialDistance < 0.6) {
                // If the distance is less than 1 km, it's considered a walking distance
                List<Node> shortestPath = LogicManager.calculateRouteByCoordinates(x1, y1, x2, y2, "walk");
                double distanceBetweenTwoZipCodes = LogicManager.calculateDistance(shortestPath);
                GUI.createMap.drawPath(shortestPath);
                TimeCalculator timeCalc = new AverageTimeCalculator(distanceBetweenTwoZipCodes);
                time = (int) (Math.round(timeCalc.getWalkingTime()));
                DataManagers.LogicManager.time = time;
                DataManagers.LogicManager.distance = distanceBetweenTwoZipCodes;
                transferModule.addTransferModule("Walk", TimeCalculator.getCurrentTime().toString(),
                        TimeCalculator.addMinutesToTime(TimeCalculator.getCurrentTime(), time).toString());

            } else {
                directBestTrip = processRoutes(conn, x1, y1, x2, y2);
                directTripTime = TimeCalculator
                        .calculateTripTime(TimeCalculator.getCurrentTime().toString(),
                                directBestTrip.getTimeOfArrDest());
                System.out.println("best direct trip is: " + directBestTrip);

                if (directBestTrip != null && directTripTime < 30) {
                    showDirectInfo(conn, directBestTrip);
                } else {
                    transferBestTrip = tempTransfer.processTransfers(x1, y1, x2, y2);
                    if ((directBestTrip != null
                            && directBestTrip.getTimeOfArrDestINMs() <= transferBestTrip.getStartFromOriginInMs())) {
                        showDirectInfo(conn, directBestTrip);
                    } else {
                        showTransferInfo(conn, transferBestTrip);
                    }

                }
            }

        } catch (

        SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showDirectInfo(Connection conn, TripInfo directBestTrip) throws SQLException {
        System.out.println("Best Trip after if: " + directBestTrip);
        queryShapeDetails(conn, directBestTrip.getTripId(), directBestTrip.getStartStopId(),
                directBestTrip.getEndStopId());
        queryStopsBetween(conn, directBestTrip.getTripId(), directBestTrip.getStartStopId(),
                directBestTrip.getEndStopId());

        if (number_shape_id == 0) {
            createMap.drawPath(stopNodes, directBestTrip.getColor());
        } else {
            createMap.drawPath(tripNodes, stopNodes, directBestTrip.getColor());
        }
        double totalDistance = calculateTotalDistance(tripNodes);
        if (totalDistance == 0) {
            totalDistance = calculateTotalDistance(stopNodes);
        }

        DataManagers.LogicManager.time = TimeCalculator
                .calculateTripTime(directBestTrip.getStartFromOrigin(), directBestTrip.getTimeOfArrDest());
        System.out.println("best direct trip is: " + directBestTrip);
        ;
        DataManagers.LogicManager.distance = totalDistance;

        transferModule.addTransferModule("Walk", directBestTrip.getStartFromOrigin(),
                directBestTrip.getStartDepartureTime());
        transferModule.addTransferModule("Bus", directBestTrip.getStartDepartureTime(),
                directBestTrip.getEndArrivalTime(), directBestTrip.getBusNumber(),
                directBestTrip.getStartStopName(),
                directBestTrip.getEndStopName());
        transferModule.addTransferModule("Walk", directBestTrip.getEndArrivalTime(),
                directBestTrip.getTimeOfArrDest());
    }

    public static void showTransferInfo(Connection conn, TripInfo tempTransfer) throws SQLException {
        // when transfer is better
        TripInfo firstTrip = Bus.tempTransfer.getFirstTrip();
        System.out.println("First Best Trip: " + firstTrip);
        System.out.println("========================================");

        queryShapeDetails(conn, firstTrip.getTripId(), firstTrip.getStartStopId(),
                firstTrip.getEndStopId());
        queryStopsBetween(conn, firstTrip.getTripId(), firstTrip.getStartStopId(),
                firstTrip.getEndStopId());
        System.out.println("1st" + firstTrip.getColor());

        double firstDistance = calculateTotalDistance(tripNodes);
        if (firstDistance == 0) {
            firstDistance = calculateTotalDistance(stopNodes);
        }

        // for second trip
        int numberOfFirstTripNode = tripNodes.size();
        number_shape_id = 0;

        // Debugging print statements
        System.out.println("Second Best Trip: " + transferBestTrip);
        System.out.println("========================================");
        queryShapeDetails(conn, transferBestTrip.getTripId(), transferBestTrip.getStartStopId(),
                transferBestTrip.getEndStopId());
        queryStopsBetween(conn, transferBestTrip.getTripId(), transferBestTrip.getStartStopId(),
                transferBestTrip.getEndStopId());
        System.out.println("2nd:" + transferBestTrip.getColor());

        if (number_shape_id == 0) {
            createMap.drawPath(stopNodes, numberOfFirstTripNode,
                    firstTrip.getColor(),
                    transferBestTrip.getColor());
            System.out.println("no shape");
        } else {
            createMap.drawPath(tripNodes, stopNodes, numberOfFirstTripNode,
                    firstTrip.getColor(),
                    transferBestTrip.getColor());
        }

        double secondDistance = calculateTotalDistance(tripNodes);
        if (secondDistance == 0) {
            secondDistance = calculateTotalDistance(stopNodes);
        }
        double totalDistance = firstDistance + secondDistance;
        System.out.println("Total Distance: " + totalDistance + " km");

        DataManagers.LogicManager.time = TimeCalculator.calculateTripTime(
                firstTrip.getStartFromOrigin(),
                transferBestTrip.getStartFromOrigin());
        DataManagers.LogicManager.distance = totalDistance;

        transferModule.addTransferModule("Walk", firstTrip.getStartFromOrigin(),
                firstTrip.getStartDepartureTime());
        transferModule.addTransferModule("Bus", firstTrip.getStartDepartureTime(),
                firstTrip.getEndArrivalTime(), firstTrip.getBusNumber(),
                firstTrip.getStartStopName(),
                firstTrip.getEndStopName());
        transferModule.addTransferModule("Bus", transferBestTrip.getStartDepartureTime(),
                transferBestTrip.getEndArrivalTime(), transferBestTrip.getBusNumber(),
                transferBestTrip.getStartStopName(),
                transferBestTrip.getEndStopName());
        transferModule.addTransferModule("Walk", transferBestTrip.getEndArrivalTime(),
                transferBestTrip.getStartFromOrigin());
    }

    /**
     * Processes the potential bus routes between two geographic coordinates.
     * 
     * @param conn the database connection
     * @param x1   the latitude of the start location
     * @param y1   the longitude of the start location
     * @param x2   the latitude of the end location
     * @param y2   the longitude of the end location
     * @return the best TripInfo object found
     * @throws SQLException if a database error occurs
     */
    public static TripInfo processRoutes(Connection conn, double x1, double y1, double x2, double y2)
            throws SQLException {
        setupNearestStops(conn, x1, y1, x2, y2);
        findPotentialRoutes(conn);
        RouteStopInfo routes = findRouteBusStops(conn);
        bestTrip = null;
        if (routes != null) {
            // units in km
            double distanceToBusstop = TimeCalculator.calculateDistanceIfNotCached(x1, y1,
                    getStopLocation(conn, routes.startStopId)[0],
                    getStopLocation(conn, routes.startStopId)[1]);
            TimeCalculator timeCalc = new AverageTimeCalculator(distanceToBusstop);
            time = (int) (Math.round(timeCalc.getWalkingTime()));

            // Get the current time
            Time currentTime = TimeCalculator.getCurrentTime();
            // Calculate the walking time to start bus stop in milliseconds
            long additionalTimeInMs = time * 60 * 1000;
            Time TimeOfArrStartBusStop = new Time(currentTime.getTime() + additionalTimeInMs);

            bestTrip = getBestTrip(conn, routes.routeId, routes.startStopId, routes.endStopId,
                    TimeOfArrStartBusStop, additionalTimeInMs, x2, y2);
        }

        return bestTrip;
    }

    /**
     * Sets up the nearest start and end stops based on the provided coordinates.
     * 
     * @param conn     the database connection
     * @param startLat the latitude of the start location
     * @param startLon the longitude of the start location
     * @param endLat   the latitude of the end location
     * @param endLon   the longitude of the end location
     * @throws SQLException if a database error occurs
     */
    public static void setupNearestStops(Connection conn, double startLat, double startLon, double endLat,
            double endLon) throws SQLException {
        String sqlDropStartStops = "DROP TABLE IF EXISTS nearest_start_stops;";
        String createNearestStartStops = """
                CREATE TEMPORARY TABLE nearest_start_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops
                where ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) < ?
                ORDER BY distance LIMIT ?
                ;""";
        String sqlDropEndStops = "DROP TABLE IF EXISTS nearest_end_stops;";
        String createNearestEndStops = """
                CREATE TEMPORARY TABLE nearest_end_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops
                where ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) < ?
                ORDER BY distance LIMIT ?
                ;""";

        try (PreparedStatement pstmt1 = conn.prepareStatement(createNearestStartStops);
                PreparedStatement pstmt2 = conn.prepareStatement(createNearestEndStops)) {
            Statement stmt1 = conn.createStatement();
            stmt1.execute(sqlDropStartStops);
            Statement stmt2 = conn.createStatement();
            stmt2.execute(sqlDropEndStops);

            pstmt1.setDouble(1, startLon);
            pstmt1.setDouble(2, startLat);
            pstmt1.setDouble(3, startLon);
            pstmt1.setDouble(4, startLat);
            pstmt2.setDouble(1, endLon);
            pstmt2.setDouble(2, endLat);
            pstmt2.setDouble(3, endLon);
            pstmt2.setDouble(4, endLat);
            if (aerialDistance < 2) {
                pstmt1.setInt(6, 12);
                pstmt2.setInt(6, 12);

            } else {
                pstmt1.setInt(6, 20);
                pstmt2.setInt(6, 20);
            }
            pstmt1.setDouble(5, aerialDistance * 1000 / 1.2);
            pstmt2.setDouble(5, aerialDistance * 1000 / 1.2);
            pstmt1.executeUpdate();
            pstmt2.executeUpdate();
        }
    }

    /**
     * Finds potential routes by creating a temporary table with route information.
     * 
     * @param conn the database connection
     * @throws SQLException if a database error occurs
     */
    public static void findPotentialRoutes(Connection conn) throws SQLException {
        String sqlDropPotentialToutes = "DROP TABLE IF EXISTS potential_routes;";
        String sql = """
                    CREATE TEMPORARY TABLE IF NOT EXISTS potential_routes AS
                    SELECT DISTINCT
                        t.route_id,
                        st1.trip_id,
                        MIN(st1.departure_time) AS earliest_departure_time,
                        MIN(nss.distance + nes.distance) AS min_total_distance,
                        SUBSTRING_INDEX(
                            GROUP_CONCAT(
                                st1.stop_id ORDER BY (nss.distance + nes.distance) ASC, st1.stop_id
                            ),
                            ',',
                            1
                        ) AS start_stop_id,
                        SUBSTRING_INDEX(
                            GROUP_CONCAT(
                                st2.stop_id ORDER BY (nss.distance + nes.distance) ASC, st2.stop_id
                            ),
                            ',',
                            1
                        ) AS end_stop_id
                    FROM
                        stop_times st1
                    JOIN
                        stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence
                    JOIN
                        trips t ON st1.trip_id = t.trip_id
                    JOIN
                        nearest_start_stops nss ON st1.stop_id = nss.stop_id
                    JOIN
                        nearest_end_stops nes ON st2.stop_id = nes.stop_id
                    WHERE
                        st1.departure_time >= CURRENT_TIME()
                    GROUP BY
                        t.route_id, st1.trip_id
                    ORDER BY
                        earliest_departure_time ASC;
                """;
        try (Statement stmt = conn.createStatement();) {
            stmt.execute(sqlDropPotentialToutes);
            stmt.execute(sql);
        }
    }

    /**
     * Finds the route bus stops from the potential routes and returns a list of
     * RouteStopInfo objects.
     * 
     * @param conn the database connection
     * @return a list of RouteStopInfo objects
     * @throws SQLException if a database error occurs
     */
    private static RouteStopInfo findRouteBusStops(Connection conn) throws SQLException {
        RouteStopInfo routes = null;
        String setGroupConcatMaxLen = "SET SESSION group_concat_max_len = 1000000;";
        String sql = "SELECT route_id, start_stop_id, end_stop_id FROM route_bus_stops;";
        String sqlDropRouteBusStops = "DROP TABLE IF EXISTS route_bus_stops;";
        String sqlRoute = """
                        CREATE TEMPORARY TABLE IF NOT EXISTS route_bus_stops AS
                    SELECT
                        pr.route_id,
                        pr.start_stop_id,
                        pr.end_stop_id,
                        MIN(st2.arrival_time) AS earliest_arrival_time
                    FROM
                        potential_routes pr
                    JOIN
                        stop_times st1 ON pr.start_stop_id = st1.stop_id AND pr.trip_id = st1.trip_id
                    JOIN
                        stop_times st2 ON pr.end_stop_id = st2.stop_id AND pr.trip_id = st2.trip_id
                    WHERE
                        st1.departure_time >= CURRENT_TIME()
                    GROUP BY
                        pr.route_id, pr.start_stop_id, pr.end_stop_id
                    ORDER BY
                        earliest_arrival_time ASC
                    LIMIT 1;
                """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(setGroupConcatMaxLen); // Extend group_concat_max_len for large datasets
            stmt.execute(sqlDropRouteBusStops);
            stmt.execute(sqlRoute);

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                routes = (new RouteStopInfo(
                        rs.getString("route_id"),
                        rs.getString("start_stop_id"),
                        rs.getString("end_stop_id")));
            }
        }
        return routes;
    }

    /**
     * Retrieves the next departure and arrival times for the given route, start
     * stop, and end stop.
     * 
     * @param conn        the database connection
     * @param routeId     the route ID
     * @param startStopId the start stop ID
     * @param endStopId   the end stop ID
     * @return a list of TripInfo objects with departure and arrival information
     * @throws SQLException if a database error occurs
     */
    private static TripInfo getBestTrip(Connection conn, String routeId, String startStopId,
            String endStopId, Time time, long additionalTimeInMs, double x2, double y2) throws SQLException {
        String sql = """
                    SELECT
                    start_stop_id,
                    s1.stop_name AS start_stop_name,
                    end_stop_id ,
                    s2.stop_name AS end_stop_name,
                    route_id,
                    route_short_name,
                    route_long_name,
                    trip_id,
                    start_departure_time,
                    end_arrival_time,
                    TIMESTAMPDIFF(MINUTE, start_departure_time, end_arrival_time) AS trip_time
                FROM
                preComputedTripDetails
                JOIN
                    stops s1 on s1.stop_id = start_stop_id
                JOIN stops s2 on s2.stop_id = end_stop_id
                WHERE
                    start_stop_id = ?
                    AND end_stop_id = ?
                    AND route_id = ?
                    AND start_departure_time >= ?
                ORDER BY
                    start_departure_time ASC
                LIMIT 1;
                    """;

        TripInfo trips = null;
        Time bestTimeToDestination = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startStopId);
            pstmt.setString(2, endStopId);
            pstmt.setString(3, routeId);
            pstmt.setTime(4, time);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Time startDepartureTime = rs.getTime("start_departure_time");
                Time TimeOfArrStartBusStop = new Time(startDepartureTime.getTime() - additionalTimeInMs);

                String endBusstopID = rs.getString("end_stop_id");

                double endStopLat = getStopLocation(conn, endBusstopID)[0];
                double endStopLon = getStopLocation(conn, endBusstopID)[1];
                double distanceToDest = TimeCalculator.calculateDistanceIfNotCached(endStopLat, endStopLon, x2, y2);
                TimeCalculator timeCalc = new AverageTimeCalculator(distanceToDest);
                int timeToDest = (int) (Math.round(timeCalc.getWalkingTime()));

                // Get the current time
                Time timeOfArrEndBusstop = rs.getTime("end_arrival_time");
                // Calculate the walking time to start bus stop in milliseconds
                long timeToDestInLong = timeToDest * 60 * 1000;
                Time TimeToDestInTime = new Time(timeOfArrEndBusstop.getTime() + timeToDestInLong);
                if (trips == null || TimeToDestInTime.before(bestTimeToDestination)) {
                    bestTimeToDestination = TimeToDestInTime;
                    trips = (new TripInfo(
                            rs.getString("route_id"),
                            rs.getString("route_short_name"), // route_id
                            rs.getString("trip_id"),
                            rs.getString("start_stop_id"),
                            rs.getString("end_stop_id"),
                            rs.getString("start_departure_time"),
                            rs.getString("end_arrival_time"),
                            rs.getInt("trip_time"),
                            TimeOfArrStartBusStop.toString(),
                            rs.getString("start_stop_name"),
                            rs.getString("end_stop_name"),
                            TimeToDestInTime.toString()));
                }

            }
        }
        return trips;
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
    private static void queryShapeDetails(Connection conn, String tripId, String startId, String endId)
            throws SQLException {
        // First, determine the latitude and longitude of the start and end stops
        String stopInfoQuery = "SELECT stop_lat, stop_lon FROM stops WHERE stop_id = ?";
        double startLat = 0, startLon = 0, endLat = 0, endLon = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(stopInfoQuery)) {
            pstmt.setString(1, startId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                startLat = rs.getDouble("stop_lat");
                startLon = rs.getDouble("stop_lon");
            }

            pstmt.setString(1, endId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                endLat = rs.getDouble("stop_lat");
                endLon = rs.getDouble("stop_lon");
            }
        }

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

                double shapePtLat = rs.getDouble("shape_pt_lat");
                double shapePtLon = rs.getDouble("shape_pt_lon");

                tripNodes.add(new Node(number_shape_id, shapePtLat, shapePtLon));
                number_shape_id++;
            }

        }
    }

    /**
     * Queries and prints the stops between the start and end stops for a given
     * trip.
     * 
     * @param conn        the database connection
     * @param tripId      the trip ID
     * @param startStopId the start stop ID
     * @param endStopId   the end stop ID
     */
    private static void queryStopsBetween(Connection conn, String tripId, String startStopId, String endStopId) {
        String sql = """
                SELECT st.trip_id, st.stop_id, s.stop_name, st.stop_sequence, s.stop_lat, s.stop_lon
                    FROM stop_times st
                    JOIN stops s ON st.stop_id = s.stop_id
                    WHERE st.trip_id = ? AND
                    st.stop_sequence >= (SELECT stop_sequence FROM stop_times WHERE trip_id = ? AND stop_id = ?) AND
                    st.stop_sequence <= (SELECT stop_sequence FROM stop_times WHERE trip_id = ? AND stop_id = ?)
                    ORDER BY st.stop_sequence;""";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tripId);
            pstmt.setString(2, tripId);
            pstmt.setString(3, startStopId);
            pstmt.setString(4, tripId);
            pstmt.setString(5, endStopId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String stopName = rs.getString("stop_name");
                if (number_stop_id == 0)
                    startBusStop = stopName;
                endBusStop = stopName;

                double stopLat = rs.getDouble("stop_lat");
                double stopLon = rs.getDouble("stop_lon");

                stopNodes.add(new Node(number_stop_id, stopLat, stopLon));
                number_stop_id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static double[] getStopLocation(Connection conn, String stopId) {
        String sql = "SELECT stop_lat, stop_lon FROM stops WHERE stop_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stopId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double lat = rs.getDouble("stop_lat");
                    double lon = rs.getDouble("stop_lon");
                    return new double[] { lat, lon };
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
        return null; // Return null if no location found or if an exception occurred
    }

}
