package transfer;

import Database.DatabaseConnection;
import java.sql.*;

public class transferMainBeforeIntegrate {

    public static void main(String[] args) {

        try (Connection con = DatabaseConnection.getConnection()) {

            // from 6227 XB to 6125 RB
            double x1 = 50.8391159;
            double y1 = 5.7342817;

            // Coordinates of the end point
            double x2 = 50.8384691;
            double y2 = 5.6469823;

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
                        stop_id,
                        intersecting_stops
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
                    String stopId = rs.getString("stop_id");
                    String intersectingStops = rs.getString("intersecting_stops");

                    // Step 2: Process each row to find the trips
                    String firstTripQuery = getFirstTripQuery();
                    TripDetails firstTrip = fetchTripDetails(con, firstTripQuery, startStopId, intersectingStops,
                            startRouteId);

                    if (firstTrip != null) {
                        String secondTripQuery = getSecondTripQuery();
                        TripDetails secondTrip = fetchTripDetails(con, secondTripQuery, intersectingStops, stopId,
                                endRouteId, firstTrip.getEndArrivalTime());

                        // Step 3: Insert the result of secondTrip into tempTransfer
                        if (secondTrip != null) {
                            insertIntoTempTransfer(con, firstTrip, secondTrip);

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
                FROM stops ORDER BY distance LIMIT 10;""";
        String sqlDropEndStops = "DROP TABLE IF EXISTS nearest_end_stops;";
        String createNearestEndStops = """
                CREATE TEMPORARY TABLE nearest_end_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops ORDER BY distance LIMIT 10;""";

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
                    er.stop_id
                FROM
                    StartRoutes sr
                JOIN
                    EndRoutes er ON sr.route_id > er.route_id;
                    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            System.out.println("Temporary table routeTransfer created successfully.");
        }
    }

    private static void createFinalRouteTransferTable(Connection conn) throws SQLException {
        String createTableQuery = """
                CREATE TEMPORARY TABLE finalRouteTransfer AS
                SELECT DISTINCT rt.*, ts.intersecting_stops
                from routeTransfer rt
                join transferStops ts on rt.start_route_id = ts.route_id_1 and rt.end_route_id = ts.route_id_2 ;
                                    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            System.out.println("Temporary table finalRouteTransfer created successfully.");
        }
    }

    private static String getFirstTripQuery() {
        return """
                SELECT
                    st1.stop_id,
                    st2.stop_id,
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
                    stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence
                JOIN
                    trips t ON t.trip_id = st1.trip_id
                JOIN
                    routes r ON t.route_id = r.route_id
                WHERE
                    st1.stop_id = ?
                    AND st2.stop_id = ?
                    AND t.route_id = ?
                    AND st1.departure_time >= '10:20:00'
                ORDER BY
                    st1.departure_time ASC
                LIMIT 1;
                """;
    }

    private static String getSecondTripQuery() {
        return """
                SELECT
                    st1.stop_id,
                    st2.stop_id,
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
                    stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence
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
                    st1.departure_time ASC
                LIMIT 1;
                """;
    }

    private static TripDetails fetchTripDetails(Connection conn, String query, Object... params) {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String) {
                    pstmt.setString(i + 1, (String) params[i]);
                } else if (params[i] instanceof Time) {
                    pstmt.setTime(i + 1, (Time) params[i]);
                } else if (params[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) params[i]);
                } else {
                    throw new IllegalArgumentException("Unsupported parameter type: " + params[i].getClass().getName());
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TripDetails(
                            rs.getString("route_id"),
                            rs.getString("route_short_name"),
                            rs.getInt("trip_id"),
                            rs.getString("st1.stop_id"), // start stop ID
                            rs.getString("st2.stop_id"), // end stop ID
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

    private static void insertIntoTempTransfer(Connection conn, TripDetails firstTrip, TripDetails secondTrip) {
        String insert = """
                INSERT INTO tempTransfer (
                    first_start_bus_stop_id, first_end_bus_stop_id, first_route_id, first_route_short_name, first_trip_id, first_departure_time, first_arrival_time, first_trip_time,
                    second_start_bus_stop_id, second_end_bus_stop_id, second_route_id, second_route_short_name, second_trip_id, second_departure_time, second_arrival_time, second_trip_time
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
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

            pstmt.setString(9, secondTrip.getStartStopId());
            pstmt.setString(10, secondTrip.getEndStopId());
            pstmt.setString(11, secondTrip.getRouteId());
            pstmt.setString(12, secondTrip.getRouteShortName());
            pstmt.setInt(13, secondTrip.getTripId());
            pstmt.setTime(14, secondTrip.getStartDepartureTime());
            pstmt.setTime(15, secondTrip.getEndArrivalTime());
            pstmt.setInt(16, secondTrip.getTripTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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
