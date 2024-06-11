package Bus;

import java.sql.*;

import java.util.*;

import Calculators.DistanceCalculatorHaversine;
import DataManagers.Node;
import Database.DatabaseConnection;
import GUI.createMap;
import javax.swing.*;

//import com.graphhopper.json.Statement;

/**
 * Manages the logic behind bus trips.
 */
public class BusConnectionDev {

    public static boolean testClass = false;

    static List<Node> stopNodes = new ArrayList<>();
    static int id = 0;

    static List<Node> tripNodes = new ArrayList<>();
    static int id2 = 0;

    static TripInfo bestTrip;
    static int time;

    static String busName = "";
    static String busNumber = "";
    static String startBusStop = "";
    static String endBusStop = "";
    static String departureTime = "";
    static String arrivalTime = "";

    /**
     * Resets the lists and IDs used for tracking stops and trips.
     */
    public static void resetLists() {
        stopNodes = new ArrayList<>();
        id = 0;
        tripNodes = new ArrayList<>();
        id2 = 0;
    }

    public static int getId2() {
        return id2;
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
            bestTrip = processRoutes(conn, x1, y1, x2, y2);

            if (bestTrip != null) {

                // Debugging print statements
                System.out.println("Best Trip: " + bestTrip);
                System.out.println("========================================");
                queryShapeDetails(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());
                System.out.println("========================================");

                queryStopsBetween(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());
                if (id2 == 0) {
                    createMap.drawPath(stopNodes);
                    System.out.println("no shape");
                } else {
                    createMap.drawPath(tripNodes, stopNodes);
                }

                double totalDistance = calculateTotalDistance(tripNodes);
                if (totalDistance == 0)
                    totalDistance = calculateTotalDistance(stopNodes);
                System.out.println("Total Distance: " + totalDistance + " km");

                DataManagers.LogicManager.time = bestTrip.getTripTime();
                DataManagers.LogicManager.distance = totalDistance;

                DataManagers.LogicManager.busInfo = new String[] {
                        bestTrip.getBusName(),
                        bestTrip.getBusNumber(),
                        startBusStop,
                        endBusStop,
                        bestTrip.endArrivalTime,
                        bestTrip.startDepartureTime };
            } else {
                // if (!testClass)
                // JOptionPane.showMessageDialog(null, "No direct bus connection");
                // testClass = false;
                // throw new Exception("No direct bus connection");
                double stationLon = 50.849932;
                double stationLan = 5.705160;
                bestTrip = processRoutes(conn, x1, y1, stationLon, stationLan);
                queryShapeDetails(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());

                queryStopsBetween(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());
                if (id2 == 0) {
                    createMap.drawPath(stopNodes);
                    System.out.println("no shape");
                } else {
                    createMap.drawPath(tripNodes, stopNodes);
                }

                double totalDistance = calculateTotalDistance(tripNodes);
                if (totalDistance == 0)
                    totalDistance = calculateTotalDistance(stopNodes);
                System.out.println("Total Distance: " + totalDistance + " km");

                DataManagers.LogicManager.time = bestTrip.getTripTime();
                DataManagers.LogicManager.distance = totalDistance;

                DataManagers.LogicManager.busInfo = new String[] {
                        bestTrip.getBusName(),
                        bestTrip.getBusNumber(),
                        startBusStop,
                        endBusStop,
                        bestTrip.endArrivalTime,
                        bestTrip.startDepartureTime };

                id2 = 0;
                bestTrip = processRoutes(conn, stationLon, stationLan, x2, y2);
                queryShapeDetails(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());

                queryStopsBetween(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());
                if (id2 == 0) {
                    createMap.drawPath(stopNodes);
                    System.out.println("no shape");
                } else {
                    createMap.drawPath(tripNodes, stopNodes);
                }

                totalDistance += calculateTotalDistance(tripNodes);
                if (totalDistance == 0)
                    totalDistance = calculateTotalDistance(stopNodes);
                System.out.println("Total Distance: " + totalDistance + " km");

                DataManagers.LogicManager.time = bestTrip.getTripTime();
                DataManagers.LogicManager.distance = totalDistance;

            }

        } catch (

        SQLException e) {
            e.printStackTrace();
        }
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
        List<RouteStopInfo> routes = findRouteBusStops(conn);
        TripInfo bestTrip = null; // Initialize with no best trip found

        for (RouteStopInfo route : routes) {
            List<TripInfo> tripsForRoute = printNextDepartureAndArrival(conn,
                    route.routeId, route.startStopId, route.endStopId);
            for (TripInfo trip : tripsForRoute) {
                // Update the best trip based on trip time and arrival time
                if (bestTrip == null || trip.getTripTime() < bestTrip.getTripTime() ||
                        (trip.getTripTime() == bestTrip.getTripTime()
                                && trip.getArrivalTime().compareTo(bestTrip.getArrivalTime()) < 0)) {
                    bestTrip = trip; // Found a new best trip
                }
            }
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
                FROM stops ORDER BY distance LIMIT 20;""";
        String sqlDropEndStops = "DROP TABLE IF EXISTS nearest_end_stops;";
        String createNearestEndStops = """
                CREATE TEMPORARY TABLE nearest_end_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops ORDER BY distance LIMIT 20;"""

        ;

        try (PreparedStatement pstmt1 = conn.prepareStatement(createNearestStartStops);
                PreparedStatement pstmt2 = conn.prepareStatement(createNearestEndStops)) {
            Statement stmt1 = conn.createStatement();
            stmt1.execute(sqlDropStartStops);
            Statement stmt2 = conn.createStatement();
            stmt2.execute(sqlDropEndStops);

            pstmt1.setDouble(1, startLon);
            pstmt1.setDouble(2, startLat);
            pstmt2.setDouble(1, endLon);
            pstmt2.setDouble(2, endLat);
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
    private static List<RouteStopInfo> findRouteBusStops(Connection conn) throws SQLException {
        List<RouteStopInfo> routes = new ArrayList<>();
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
                routes.add(new RouteStopInfo(
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
    private static List<TripInfo> printNextDepartureAndArrival(Connection conn, String routeId, String startStopId,
            String endStopId) throws SQLException {
        String sql = "SELECT t.route_id, r.route_short_name, r.route_long_name, st1.trip_id, st1.departure_time AS start_departure_time, st2.arrival_time AS end_arrival_time, "
                + "TIMESTAMPDIFF(MINUTE, st1.departure_time, st2.arrival_time) AS trip_time "
                + "FROM stop_times st1 "
                + "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence "
                + "JOIN trips t ON t.trip_id = st1.trip_id "
                + "JOIN routes r ON t.route_id = r.route_id "
                + "WHERE st1.stop_id = ? AND st2.stop_id = ? AND t.route_id = ? AND st1.departure_time >= CURRENT_TIME() "
                + "ORDER BY CASE WHEN st1.departure_time >= CURRENT_TIME() THEN 0 ELSE 1 END, st1.departure_time ASC "
                + "LIMIT 1;";

        List<TripInfo> trips = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startStopId);
            pstmt.setString(2, endStopId);
            pstmt.setString(3, routeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                trips.add(new TripInfo(
                        rs.getString("route_id"),
                        rs.getString("route_short_name"),
                        rs.getString("route_long_name"),
                        rs.getString("trip_id"),
                        startStopId,
                        endStopId,
                        rs.getString("start_departure_time"),
                        rs.getString("end_arrival_time"),
                        rs.getInt("trip_time")));
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
                String shapeId = rs.getString("shape_id");
                int shapePtSequence = rs.getInt("shape_pt_sequence");
                double shapePtLat = rs.getDouble("shape_pt_lat");
                double shapePtLon = rs.getDouble("shape_pt_lon");

                System.out.println("Shape ID: " + shapeId +
                        ", Shape Pt Sequence: " + shapePtSequence +
                        ", Latitude: " + shapePtLat +
                        ", Longitude: " + shapePtLon);

                tripNodes.add(new Node(id2, shapePtLat, shapePtLon));
                id2++;
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
                String stopId = rs.getString("stop_id");
                String stopName = rs.getString("stop_name");
                if (id == 0)
                    startBusStop = stopName;
                endBusStop = stopName;

                int stopSequence = rs.getInt("stop_sequence");
                double stopLat = rs.getDouble("stop_lat");
                double stopLon = rs.getDouble("stop_lon");

                System.out.println("Trip ID: " + tripId + ", Stop ID: " + stopId + ", Stop Name: " + stopName +
                        ", Stop Sequence: " + stopSequence + ", Latitude: " + stopLat + ", Longitude: " + stopLon);

                stopNodes.add(new Node(id, stopLat, stopLon));
                id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
