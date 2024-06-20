
package Bus;

import java.sql.*;

import java.util.*;

import Calculators.AverageTimeCalculator;
import Calculators.DistanceCalculatorHaversine;
import Calculators.TimeCalculator;
import DataManagers.LogicManager;
import DataManagers.Node;
import Database.DatabaseConnection;
import GUI.transferModule;

// Manages the logic behind bus trips.

public class BusConnectionTest {

    public static boolean testClass = false;

    static List<Node> stopNodes = new ArrayList<>();
    static int id = 0;

    static List<Node> tripNodes = new ArrayList<>();
    static int id2 = 0;

    static TripInfo bestTrip;
    static TripInfo transferBestTrip;
    static TripInfo firstTrip;
    static int time;

    static String busName = "";
    static String busNumber = "";
    static String startBusStop = "";
    static String endBusStop = "";
    static String departureTime = "";
    static String arrivalTime = "";

    static double distanceBetweenTwoZipCodes = 0;

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
            totalDistance += DistanceCalculatorHaversine.haversineDistance(nodes.get(i),
                    nodes.get(i + 1));
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
     * @throws Exception if no direct bus connection is found or if a database
     *                   error
     *                   occurs
     */
    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection()) {

            String fetchZipCodesQuery = "SELECT * FROM post_codes_join_table;";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(fetchZipCodesQuery);
            while (rs.next()) {
                String startZip = rs.getString(1);
                double startLat = rs.getDouble(2);
                double startLon = rs.getDouble(3);
                String endZip = rs.getString(4);
                double endLat = rs.getDouble(5);
                double endLon = rs.getDouble(6);
                distanceBetweenTwoZipCodes = Calculators.DistanceCalculatorHaversine.calculate(startLat,
                        startLon,
                        endLat,
                        endLon);

                Time setDepartureTime = Time.valueOf(String.format("10:%02d:00", 30));
                // List<Node> shortestPath = LogicManager.calculateRouteByCoordinates(startLat,
                // startLon,
                // endLat,
                // endLon, "walk");
                // distanceBetweenTwoZipCodes = LogicManager.calculateDistance(shortestPath);
                TimeCalculator timeCalc = new AverageTimeCalculator(distanceBetweenTwoZipCodes);

                time = (int) (Math.round(timeCalc.getWalkingTime()));
                if (time > 10) {
                    // If the distance is less than 1 km, it's considered a walking distance

                    transferBestTrip = tempTransfer.processTransfers(startLat,
                            startLon,
                            endLat,
                            endLon, 1000);
                    firstTrip = tempTransfer.getFirstTrip();

                    String insert = """
                            INSERT INTO RouteSixToSixThirty (
                            start_zipcode, end_zipcode, departure_time,
                            first_start_bus_stop_id, first_end_bus_stop_id, first_route_id,
                            first_route_short_name, first_trip_id,
                            first_departure_time, first_arrival_time, first_trip_time,
                            second_start_bus_stop_id, second_end_bus_stop_id, second_route_id,
                            second_route_short_name,
                            second_trip_id, second_departure_time, second_arrival_time, second_trip_time
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                            """;
                    try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                        pstmt.setString(1, startZip);
                        pstmt.setString(2, endZip);
                        pstmt.setTime(3, setDepartureTime);

                        pstmt.setString(4, firstTrip.getStartStopId());
                        pstmt.setString(5, firstTrip.getEndStopId());
                        pstmt.setString(6, firstTrip.getRouteId());
                        pstmt.setString(7, firstTrip.getBusNumber());
                        pstmt.setString(8, firstTrip.getTripId());
                        pstmt.setString(9, firstTrip.getStartDepartureTime());
                        pstmt.setString(10, firstTrip.getEndArrivalTime());
                        pstmt.setInt(11, firstTrip.getTripTime());

                        pstmt.setString(12, transferBestTrip.getStartStopId());
                        pstmt.setString(13, transferBestTrip.getEndStopId());
                        pstmt.setString(14, transferBestTrip.getRouteId());
                        pstmt.setString(15, transferBestTrip.getBusNumber());
                        pstmt.setString(16, transferBestTrip.getTripId());
                        pstmt.setString(17, transferBestTrip.getStartDepartureTime());
                        pstmt.setString(18, transferBestTrip.getEndArrivalTime());
                        pstmt.setInt(19, transferBestTrip.getTripTime());

                        pstmt.executeUpdate();
                        System.out.println("insert successfully");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
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
    public static TripInfo processRoutes(Connection conn, double x1, double y1,
            double x2, double y2,
            Time setDepartureTime)
            throws SQLException {
        setupNearestStops(conn, x1, y1, x2, y2);
        findPotentialRoutes(conn, setDepartureTime);
        RouteStopInfo routes = findRouteBusStops(conn, setDepartureTime);
        TripInfo bestTrip = null;
        if (routes != null) {

            bestTrip = printNextDepartureAndArrival(conn, routes.routeId,
                    routes.startStopId, routes.endStopId,
                    setDepartureTime);
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
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?),
                point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops
                where ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat))
                < ?
                ORDER BY distance LIMIT ?
                ;""";
        String sqlDropEndStops = "DROP TABLE IF EXISTS nearest_end_stops;";
        String createNearestEndStops = """
                CREATE TEMPORARY TABLE nearest_end_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?),
                point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops
                where ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat))
                < ?
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
            if (distanceBetweenTwoZipCodes < 1) {
                pstmt1.setInt(6, 10);
                pstmt2.setInt(6, 10);

            } else {
                pstmt1.setInt(6, 20);
                pstmt2.setInt(6, 20);
            }
            pstmt1.setDouble(5, distanceBetweenTwoZipCodes * 1000 / 2);
            pstmt2.setDouble(5, distanceBetweenTwoZipCodes * 1000 / 2);
            pstmt1.executeUpdate();
            pstmt2.executeUpdate();
        }
    }

    /**
     * Finds potential routes by creating a temporary table with route
     * information.
     *
     * @param conn the database connection
     * @throws SQLException if a database error occurs
     */
    public static void findPotentialRoutes(Connection conn, Time setDepartureTime) throws SQLException {
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
                stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence <
                st2.stop_sequence
                JOIN
                trips t ON st1.trip_id = t.trip_id
                JOIN
                nearest_start_stops nss ON st1.stop_id = nss.stop_id
                JOIN
                nearest_end_stops nes ON st2.stop_id = nes.stop_id
                WHERE
                st1.departure_time >= ?
                GROUP BY
                t.route_id, st1.trip_id
                ORDER BY
                earliest_departure_time ASC;
                """;
        try (Statement stmt = conn.createStatement();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            stmt.execute(sqlDropPotentialToutes);
            pstmt.setTime(1, setDepartureTime);
            pstmt.executeUpdate();

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
    private static RouteStopInfo findRouteBusStops(Connection conn, Time setDepartureTime) throws SQLException {
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
                st1.departure_time >= ?
                GROUP BY
                pr.route_id, pr.start_stop_id, pr.end_stop_id
                ORDER BY
                earliest_arrival_time ASC
                LIMIT 1;
                """;
        try (Statement stmt = conn.createStatement();
                PreparedStatement pstmt = conn.prepareStatement(sqlRoute)) {

            stmt.execute(setGroupConcatMaxLen); // Extend group_concat_max_len for large datasets
            stmt.execute(sqlDropRouteBusStops);
            pstmt.setTime(1, setDepartureTime);
            pstmt.executeUpdate();
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
    private static TripInfo printNextDepartureAndArrival(Connection conn, String routeId, String startStopId,
            String endStopId, Time setDepartureTime) throws SQLException {
        String sql = """
                SELECT
                t.route_id,
                r.route_short_name,
                r.route_long_name,
                st1.trip_id,
                st1.departure_time AS start_departure_time,
                st2.arrival_time AS end_arrival_time,
                TIMESTAMPDIFF(MINUTE, st1.departure_time, st2.arrival_time) AS trip_time
                FROM
                stop_times st1
                JOIN
                stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence <
                st2.stop_sequence
                JOIN
                trips t ON t.trip_id = st1.trip_id
                JOIN
                routes r ON t.route_id = r.route_id
                WHERE
                st1.stop_id = ?
                AND st2.stop_id = ?
                AND t.route_id = ?
                AND st1.departure_time >= ?
                ORDER BY
                CASE WHEN st1.departure_time >= ? THEN 0 ELSE 1 END,
                st1.departure_time ASC
                LIMIT 1;
                """;

        TripInfo trips = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startStopId);
            pstmt.setString(2, endStopId);
            pstmt.setString(3, routeId);
            pstmt.setTime(4, setDepartureTime);
            pstmt.setTime(5, setDepartureTime);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                trips = (new TripInfo(
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

}