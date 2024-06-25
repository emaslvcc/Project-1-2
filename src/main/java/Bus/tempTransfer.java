package Bus;

import Database.DatabaseConnection;
import java.sql.*;

import Calculators.*;

public class tempTransfer {
    static boolean stop = false;
    static Time newTime;

    public static TripInfo processTransfers(double x1, double y1, double x2, double y2, long directTripToDest)
            throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            int insertTime = 0;
            boolean stop = false;
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
                long startTime = System.currentTimeMillis(); // Start time
                long timeLimit = 19000; // 19s Time limit in milliseconds

                while (rs.next() && !stop && (System.currentTimeMillis() - startTime < timeLimit) && insertTime < 450) {
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

                    Time currentTime = TimeCalculator.getCurrentTime();// Base time
                    // Step 2: Process each row to find the trips
                    String firstTripQuery = getFirstTripQuery();
                    // TripInfo firstTrip = null;
                    TripInfo firstTrip = fetchTripDetails(con, firstTripQuery, startStopId, stop1ID,
                            startRouteId, currentTime);

                    if (firstTrip != null) {
                        double distanceToStartBusstop = TimeCalculator.calculateDistanceIfNotCached(x1,
                                y1, startStopLat, startStopLon);
                        TimeCalculator timeCalc = new AverageTimeCalculator(distanceToStartBusstop);
                        int time = (int) (Math.round(timeCalc.getWalkingTime()));
                        long walkingTimeToStartBusstop = time * 60 * 1000;

                        newTime = new Time(currentTime.getTime() + walkingTimeToStartBusstop);
                        firstTrip = fetchTripDetails(con, firstTripQuery, startStopId, stop1ID,
                                startRouteId, newTime);

                        String secondTripQuery = getSecondTripQuery();
                        TripInfo secondTrip = null;
                        if (firstTrip != null) {
                            secondTrip = fetchTripDetails(con, secondTripQuery, stop2ID, endStopId,
                                    endRouteId, firstTrip.getEndArrivalTime());

                            // Step 3: Insert the result of secondTrip into tempTransfer
                            if (secondTrip != null) {

                                double distanceToDest = TimeCalculator.calculateDistanceIfNotCached(endStopLat,
                                        endStopLon,
                                        x2, y2);
                                timeCalc = new AverageTimeCalculator(distanceToDest);

                                int timeToDestination = (int) (Math.round(timeCalc.getWalkingTime()));
                                insertIntoTempTransfer(con, firstTrip, secondTrip, timeToDestination,
                                        distanceToStartBusstop, walkingTimeToStartBusstop, directTripToDest);
                                insertTime++;
                                System.out.println(insertTime);

                            }

                        }

                    }

                }
                System.out.println(System.currentTimeMillis() - startTime);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getTransferBestTrip();
    }

    public static TripInfo getFirstTrip() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        TripInfo firstTrip = null;

        // First try with direct transfers where routes are the same
        String sqlGetDirect = """
                SELECT tt.*, s1.stop_name AS start_busstop_name, s2.stop_name AS end_busstop_name
                FROM tempTransfer tt
                JOIN stops s1 ON s1.stop_id = tt.first_start_bus_stop_id
                JOIN stops s2 ON s2.stop_id = tt.first_end_bus_stop_id
                WHERE tt.timeOfArrDestination = (
                    SELECT MIN(timeOfArrDestination) FROM tempTransfer
                ) AND tt.first_route_id = tt.second_route_id
                LIMIT 1;
                """;

        // Query for the earliest arrival time overall
        String sqlGetEarliestArrTime = """
                SELECT tt.*, s1.stop_name AS start_busstop_name, s2.stop_name AS end_busstop_name
                FROM tempTransfer tt
                JOIN stops s1 ON s1.stop_id = tt.first_start_bus_stop_id
                JOIN stops s2 ON s2.stop_id = tt.first_end_bus_stop_id
                ORDER BY
                timeOfArrDestination ASC, distanceToFirstBusstop ASC
                LIMIT 1;
                """;

        try (Statement stmt = conn.createStatement()) {
            // First execute the direct transfers query
            ResultSet rs = stmt.executeQuery(sqlGetDirect);
            if (rs.next()) {
                firstTrip = createFirstTripInfo(rs);
            } else {
                // If no result from direct transfers, then check for earliest arrival
                rs = stmt.executeQuery(sqlGetEarliestArrTime);
                if (rs.next()) {
                    firstTrip = createFirstTripInfo(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Or handle more gracefully
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return firstTrip;
    }

    public static TripInfo getTransferBestTrip() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        TripInfo transferBestTrip = null;

        // First try with direct transfers where routes are the same
        String sqlGetDirect = """
                SELECT tt.*, s1.stop_name AS start_busstop_name, s2.stop_name AS end_busstop_name
                FROM tempTransfer tt
                JOIN stops s1 ON s1.stop_id = tt.second_start_bus_stop_id
                JOIN stops s2 ON s2.stop_id = tt.second_end_bus_stop_id
                WHERE tt.timeOfArrDestination = (
                    SELECT MIN(timeOfArrDestination) FROM tempTransfer)
                AND tt.first_route_id = tt.second_route_id
                LIMIT 1;
                """;

        // Query for the earliest arrival time overall
        String sqlGetEarliestArrTime = """
                SELECT tt.*, s1.stop_name AS start_busstop_name, s2.stop_name AS end_busstop_name
                FROM tempTransfer tt
                JOIN stops s1 ON s1.stop_id = tt.second_start_bus_stop_id
                JOIN stops s2 ON s2.stop_id = tt.second_end_bus_stop_id
                ORDER BY
                timeOfArrDestination ASC, distanceToFirstBusstop ASC
                LIMIT 1;
                """;

        try (Statement stmt = conn.createStatement()) {
            // First execute the direct transfers query
            ResultSet rs = stmt.executeQuery(sqlGetDirect);
            if (rs.next()) {
                transferBestTrip = createSecondTripInfo(rs);
            } else {
                // If no result from direct transfers, then check for earliest arrival
                rs = stmt.executeQuery(sqlGetEarliestArrTime);
                if (rs.next()) {
                    transferBestTrip = createSecondTripInfo(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Or handle more gracefully
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return transferBestTrip;
    }

    private static TripInfo createFirstTripInfo(ResultSet rs) throws SQLException {
        return new TripInfo(
                rs.getString("first_route_id"),
                rs.getString("first_route_short_name"),
                rs.getString("first_trip_id"),
                rs.getString("first_start_bus_stop_id"),
                rs.getString("first_end_bus_stop_id"),
                rs.getString("first_departure_time"),
                rs.getString("first_arrival_time"),
                rs.getInt("first_trip_time"),
                rs.getString("timeOfDepart"),
                rs.getString("start_busstop_name"),
                rs.getString("end_busstop_name"));
    }

    // Utility method to create TripInfo from ResultSet
    private static TripInfo createSecondTripInfo(ResultSet rs) throws SQLException {
        return new TripInfo(
                rs.getString("second_route_id"),
                rs.getString("second_route_short_name"),
                rs.getString("second_trip_id"),
                rs.getString("second_start_bus_stop_id"),
                rs.getString("second_end_bus_stop_id"),
                rs.getString("second_departure_time"),
                rs.getString("second_arrival_time"),
                rs.getInt("second_trip_time"),
                rs.getString("timeOfArrDestination"),
                rs.getString("start_busstop_name"),
                rs.getString("end_busstop_name"));
    }

    public static void setupNearestStops(Connection conn, double startLat, double startLon, double endLat,
            double endLon) throws SQLException {
        String sqlDropStartStops = "DROP TABLE IF EXISTS nearest_start_stops;";
        String createNearestStartStops = """
                CREATE TEMPORARY TABLE nearest_start_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops ORDER BY distance LIMIT 16;""";
        String sqlDropEndStops = "DROP TABLE IF EXISTS nearest_end_stops;";
        String createNearestEndStops = """
                CREATE TEMPORARY TABLE nearest_end_stops AS
                SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance
                FROM stops ORDER BY distance LIMIT 16;""";

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
                    st1.stop_id AS start_stop_id,
                    st2.stop_id AS end_stop_id,
                    t.route_id,
                    r.route_short_name,
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
                    CASE WHEN st1.departure_time >= ? THEN 0 ELSE 1 END,
                    st1.departure_time ASC
                LIMIT 1;
                                """;
    }

    private static String getSecondTripQuery() {

        return """
                    SELECT
                    st1.stop_id AS start_stop_id,
                    st2.stop_id AS end_stop_id,
                    t.route_id,
                    r.route_short_name,
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
                    CASE WHEN st1.departure_time >= ? THEN 0 ELSE 1 END,
                    st1.departure_time ASC
                LIMIT 1;
                                """;
    }

    private static TripInfo fetchTripDetails(Connection conn, String query, Object... params) {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]); // Simplified setting parameters

            }
            pstmt.setTime(5, newTime);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TripInfo(
                            rs.getString("route_id"),
                            rs.getString("route_short_name"),
                            rs.getString("trip_id"),
                            rs.getString("start_stop_id"), // start stop ID
                            rs.getString("end_stop_id"), // end stop ID
                            rs.getString("start_departure_time"),
                            rs.getString("end_arrival_time"),
                            rs.getInt("trip_time"),
                            params[params.length - 1].toString());

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static void insertIntoTempTransfer(Connection conn, TripInfo firstTrip, TripInfo secondTrip,
            int timeToDestination, double distanceToStartBusstop, long walkingTimeToStartBusstop,
            long directTripToDest) {
        String insert = """
                INSERT INTO tempTransfer (
                    first_start_bus_stop_id, first_end_bus_stop_id, first_route_id, first_route_short_name, first_trip_id, first_departure_time, first_arrival_time, first_trip_time,
                    second_start_bus_stop_id, second_end_bus_stop_id, second_route_id, second_route_short_name, second_trip_id, second_departure_time, second_arrival_time, second_trip_time,distanceToFirstBusstop,timeOfArrDestination,timeOfDepart
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(insert)) {

            pstmt.setString(1, firstTrip.getStartStopId());
            pstmt.setString(2, firstTrip.getEndStopId());
            pstmt.setString(3, firstTrip.getRouteId());
            pstmt.setString(4, firstTrip.getBusNumber());
            pstmt.setString(5, firstTrip.getTripId());
            pstmt.setString(6, firstTrip.getStartDepartureTime());
            pstmt.setString(7, firstTrip.getEndArrivalTime());
            pstmt.setInt(8, firstTrip.getTripTime());

            long tDepart = firstTrip.getDepartTimeInMs();
            Time timeOfDepart = new Time(tDepart - walkingTimeToStartBusstop);

            long timeInMs = secondTrip.getArrTimeInMs(); // Get time in milliseconds since
            Time timeOfSecondTripArr = new Time(timeInMs);
            long timeToAdd = timeToDestination * 60 * 1000; // Convert minutes to milliseconds

            // Create a new Time object with the added time
            Time timeOfArrDestination = new Time(timeInMs + timeToAdd);
            if (timeOfArrDestination.getTime() < directTripToDest) {
                stop = true;
            }
            // Use the new Time object in your PreparedStatement

            pstmt.setString(9, secondTrip.getStartStopId());
            pstmt.setString(10, secondTrip.getEndStopId());
            pstmt.setString(11, secondTrip.getRouteId());
            pstmt.setString(12, secondTrip.getBusNumber());
            pstmt.setString(13, secondTrip.getTripId());
            pstmt.setString(14, secondTrip.getStartDepartureTime());
            pstmt.setTime(15, timeOfSecondTripArr);
            pstmt.setInt(16, secondTrip.getTripTime());
            pstmt.setDouble(17, distanceToStartBusstop);
            pstmt.setTime(18, timeOfArrDestination);
            pstmt.setTime(19, timeOfDepart);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // }

}
