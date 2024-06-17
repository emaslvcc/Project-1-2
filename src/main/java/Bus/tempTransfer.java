package Bus;

import Database.DatabaseConnection;
import java.sql.*;
import java.util.List;

import Calculators.AverageTimeCalculator;
import Calculators.DistanceCalculatorHaversine;
import Calculators.TimeCalculator;
import DataManagers.LogicManager;
import DataManagers.Node;

public class tempTransfer {
    private static DistanceCache distanceCache = new DistanceCache();

    public static void processTransfers(double x1, double y1, double x2, double y2) { // Initialize database connection
        try (Connection con = DatabaseConnection.getConnection()) {
            /*
             * from 6227 XB to 6125 RB
             * double x1 = 50.8391159;
             * double y1 = 5.7342817;
             * 
             * // Coordinates of the end point
             * double x2 = 50.8384691;
             * double y2 = 5.6469823;
             */

            /*
             * from 6227 XB to 6223BJ
             * double x1 = 50.8391159;
             * double y1 = 5.7342817;
             * 
             * // Coordinates of the end point
             * double x2 = 50.877973;
             * double y2 = 5.687432;
             */

            /*
             * from 6213NE to 6222nk
             * double x1 = 50.829421;
             * double y1 = 5.663643;
             * 
             * // Coordinates of the end point
             * double x2 = 50.8777704434;
             * double y2 = 5.722604;
             */

            setupNearestStops(con, x1, y1, x2, y2);
            createRouteTransferTable(con);
            createFinalRouteTransferTable(con);

            // Step 1: Read all data from finalRouteTransfer
            String fetchFinalRouteTransfer = """
                    SELECT
                        start_route_id,
                        start_stop_id,
                        end_route_id,
                        end_stop_id,
                        stop_1_id,
                        stop_2_id,
                        start_stop_lat,
                        start_stop_lon,
                        end_stop_lat,
                        end_stop_lon
                    FROM
                        finalRouteTransfer;
                    """;

            try (Statement stmt = con.createStatement();
                    Statement stmt1 = con.createStatement();
                    ResultSet rs = stmt.executeQuery(fetchFinalRouteTransfer)) {

                // Create the tempTransfer table
                stmt1.executeUpdate("TRUNCATE table tempTransfer; ");
                while (rs.next()) {
                    String startRouteId = rs.getString("start_route_id");
                    String startStopId = rs.getString("start_stop_id");
                    String endRouteId = rs.getString("end_route_id");
                    String endStopId = rs.getString("end_stop_id");
                    String stop1ID = rs.getString("stop_1_id");
                    String stop2ID = rs.getString("stop_2_id");
                    double startStopLat = rs.getDouble("start_stop_lat");
                    double startStopLon = rs.getDouble("start_stop_lon");
                    double endStopLat = rs.getDouble("end_stop_lat");
                    double endStopLon = rs.getDouble("end_stop_lon");

                    Time baseTime = Time.valueOf("10:20:00"); // Base time

                    // Step 2: Process each row to find the trips
                    String firstTripQuery = getFirstTripQuery();
                    TripDetails firstTrip = fetchTripDetails(con, firstTripQuery, startStopId, stop1ID,
                            startRouteId, baseTime);

                    if (firstTrip != null) {

                        double distanceToStartBusstop = calculateDistanceIfNotCached(x1, y1, startStopLat,
                                startStopLon);

                        TimeCalculator timeCalc = new AverageTimeCalculator(distanceToStartBusstop);
                        int time = (int) (Math.round(timeCalc.getWalkingTime()));
                        long baseTimeInMs = baseTime.getTime();
                        long additionalTimeInMs = time * 60 * 1000;
                        Time newTime = new Time(baseTimeInMs + additionalTimeInMs);
                        firstTrip = fetchTripDetails(con, firstTripQuery, startStopId, stop1ID,
                                startRouteId, newTime);

                        String secondTripQuery = getSecondTripQuery();
                        TripDetails secondTrip = fetchTripDetails(con, secondTripQuery, stop2ID, endStopId,
                                endRouteId, firstTrip.getEndArrivalTime());

                        // Step 3: Insert the result of secondTrip into tempTransfer
                        if (secondTrip != null) {

                            double distanceToDest = calculateDistanceIfNotCached(endStopLat, endStopLon, x2, y2);

                            timeCalc = new AverageTimeCalculator(distanceToDest);
                            int timeToDestination = (int) (Math.round(timeCalc.getWalkingTime()));
                            // int timeToDestination = 0;
                            insertIntoTempTransfer(con, firstTrip, secondTrip, timeToDestination,
                                    distanceToStartBusstop);

                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setupNearestStops(Connection conn, double startLat, double startLon, double endLat,
            double endLon) throws SQLException {
        String sqlDropStartStops = "DROP TABLE IF EXISTS nearest_start_stops;";
        String createNearestStartStops = """
                CREATE TEMPORARY TABLE nearest_start_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops ORDER BY distance LIMIT 12;""";
        String sqlDropEndStops = "DROP TABLE IF EXISTS nearest_end_stops;";
        String createNearestEndStops = """
                CREATE TEMPORARY TABLE nearest_end_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops ORDER BY distance LIMIT 12;""";

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

    private static void createRouteTransferTable(Connection conn) throws SQLException {
        String createTableQuery = """
                CREATE TEMPORARY TABLE routeTransfer AS
                WITH StartRoutes AS (
                    SELECT
                        DISTINCT r.route_id, nss.stop_id
                    FROM
                        nearest_start_stops nss
                    JOIN
                        stop_times st ON nss.stop_id = st.stop_id
                    JOIN
                        trips t ON st.trip_id = t.trip_id
                    JOIN
                        routes r ON t.route_id = r.route_id
                ),
                EndRoutes AS (
                    SELECT
                        DISTINCT r.route_id, nes.stop_id
                    FROM
                        nearest_end_stops nes
                    JOIN
                        stop_times st ON nes.stop_id = st.stop_id
                    JOIN
                        trips t ON st.trip_id = t.trip_id
                    JOIN
                        routes r ON t.route_id = r.route_id
                )
                SELECT
                    sr.route_id AS start_route_id,
                    sr.stop_id As start_stop_id,
                    er.route_id AS end_route_id,
                    er.stop_id As end_stop_id
                FROM
                    StartRoutes sr
                JOIN
                    EndRoutes er;
                    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            System.out.println("Temporary table routeTransfer created successfully.");
        }
    }

    private static void createFinalRouteTransferTable(Connection conn) throws SQLException {
        String createTableQuery = """
                CREATE TEMPORARY TABLE finalRouteTransfer AS
                SELECT DISTINCT
                    rt.*,
                    ats.stop_1_id,
                    ats.stop_2_id,
                    s1.stop_lat AS start_stop_lat,
                    s1.stop_lon AS start_stop_lon,
                    s2.stop_lat AS end_stop_lat,
                    s2.stop_lon AS end_stop_lon
                FROM
                    routeTransfer rt
                JOIN
                    AllTransferStops ats ON rt.start_route_id = ats.route_id_1 AND rt.end_route_id = ats.route_id_2
                JOIN
                    stops s1 ON rt.start_stop_id = s1.stop_id
                JOIN
                    stops s2 ON rt.end_stop_id = s2.stop_id;
                                                                    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            System.out.println("Temporary table finalRouteTransfer created successfully.");
        }
    }

    private static String getFirstTripQuery() {
        return """
                    SELECT
                    start_stop_id,
                    end_stop_id,
                    route_id,
                    route_short_name,
                    route_long_name,
                    trip_id,
                    start_departure_time,
                    end_arrival_time,
                    TIMESTAMPDIFF(MINUTE, start_departure_time, end_arrival_time) AS trip_time
                FROM
                preComputedTripDetails
                WHERE
                    start_stop_id = ?
                    AND end_stop_id = ?
                    AND route_id = ?
                    AND start_departure_time >= ?
                ORDER BY
                    start_departure_time ASC
                LIMIT 1;
                        """;
    }

    private static String getSecondTripQuery() {

        return """
                    SELECT
                    start_stop_id,
                    end_stop_id,
                    route_id,
                    route_short_name,
                    route_long_name,
                    trip_id,
                    start_departure_time,
                    end_arrival_time,
                    TIMESTAMPDIFF(MINUTE, start_departure_time, end_arrival_time) AS trip_time
                FROM
                preComputedTripDetails
                WHERE
                    start_stop_id = ?
                    AND end_stop_id = ?
                    AND route_id = ?
                    AND start_departure_time > ?
                ORDER BY
                    start_departure_time ASC
                LIMIT 1;

                        """;
    }

    private static TripDetails fetchTripDetails(Connection conn, String query, Object... params) {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]); // Simplified setting parameters

            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TripDetails(
                            rs.getString("route_id"),
                            rs.getString("route_short_name"),
                            rs.getInt("trip_id"),
                            rs.getString("start_stop_id"), // start stop ID
                            rs.getString("end_stop_id"), // end stop ID
                            rs.getTime("start_departure_time"),
                            rs.getTime("end_arrival_time"),
                            rs.getInt("trip_time"));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static void insertIntoTempTransfer(Connection conn, TripDetails firstTrip, TripDetails secondTrip,
            int timeToDestination, double distanceToStartBusstop) {
        String insert = """
                INSERT INTO tempTransfer (
                    first_start_bus_stop_id, first_end_bus_stop_id, first_route_id, first_route_short_name, first_trip_id, first_departure_time, first_arrival_time, first_trip_time,
                    second_start_bus_stop_id, second_end_bus_stop_id, second_route_id, second_route_short_name, second_trip_id, second_departure_time, second_arrival_time, second_trip_time,distanceToFirstBusstop
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(insert)) {

            pstmt.setString(1, firstTrip.getStartStopId());
            pstmt.setString(2, firstTrip.getEndStopId());
            pstmt.setString(3, firstTrip.getRouteId());
            pstmt.setString(4, firstTrip.getRouteShortName());
            pstmt.setInt(5, firstTrip.getTripId());
            pstmt.setTime(6, firstTrip.getStartDepartureTime());
            pstmt.setTime(7, firstTrip.getEndArrivalTime());
            pstmt.setInt(8, firstTrip.getTripTime());

            long timeInMs = secondTrip.getEndArrivalTime().getTime(); // Get time in milliseconds since the epoch
            long timeToAdd = timeToDestination * 60 * 1000; // Convert minutes to milliseconds

            // Create a new Time object with the added time
            Time newTime = new Time(timeInMs + timeToAdd);

            // Use the new Time object in your PreparedStatement
            pstmt.setTime(15, newTime);

            pstmt.setString(9, secondTrip.getStartStopId());
            pstmt.setString(10, secondTrip.getEndStopId());
            pstmt.setString(11, secondTrip.getRouteId());
            pstmt.setString(12, secondTrip.getRouteShortName());
            pstmt.setInt(13, secondTrip.getTripId());
            pstmt.setTime(14, secondTrip.getStartDepartureTime());
            pstmt.setTime(15, newTime);
            pstmt.setInt(16, secondTrip.getTripTime());
            pstmt.setDouble(17, distanceToStartBusstop);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double calculateDistanceIfNotCached(double startLat, double startLon, double endLat, double endLon) {
        Double cachedDistance = distanceCache.getDistance(startLat, startLon, endLat, endLon);
        if (cachedDistance != null) {
            return cachedDistance;
        } else {
            List<Node> path = LogicManager.calculateRouteByCoordinates(startLat, startLon, endLat, endLon, "walk");
            double distance = LogicManager.calculateDistance(path);
            distanceCache.putDistance(startLat, startLon, endLat, endLon, distance);
            return distance;
        }
    }

    // }

}

class TripDetails {
    private final String routeId;
    private final String routeShortName;
    private final int tripId;
    private final String startStopId;
    private final String endStopId;
    private final Time startDepartureTime;
    private final Time endArrivalTime;
    private final int tripTime;
    private final double distanceToFirstBusstop;

    public TripDetails(String routeId, String routeShortName, int tripId,
            String startStopId, String endStopId, Time startDepartureTime, Time endArrivalTime, int tripTime,
            double distanceToFirstBusstop) {
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startDepartureTime = startDepartureTime;
        this.endArrivalTime = endArrivalTime;
        this.tripTime = tripTime;
        this.distanceToFirstBusstop = distanceToFirstBusstop;
    }

    public TripDetails(String routeId, String routeShortName, int tripId,
            String startStopId, String endStopId, Time startDepartureTime, Time endArrivalTime, int tripTime) {
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startDepartureTime = startDepartureTime;
        this.endArrivalTime = endArrivalTime;
        this.tripTime = tripTime;
        this.distanceToFirstBusstop = 0;
    }

    public double getDistanceToFirstBusstop() {
        return distanceToFirstBusstop;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public int getTripId() {
        return tripId;
    }

    public String getStartStopId() {
        return startStopId;
    }

    public String getEndStopId() {
        return endStopId;
    }

    public Time getStartDepartureTime() {
        return startDepartureTime;
    }

    public Time getEndArrivalTime() {
        return endArrivalTime;
    }

    public int getTripTime() {
        return tripTime;
    }
}
