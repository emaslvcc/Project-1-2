package transfer;

import java.sql.*;
import java.util.*;

import Database.DatabaseConnection;

public class findIntersectBusstops {
    public static void main(String[] args) {
        RouteProcessing();

    }

    static public void RouteProcessing() {
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement()) {

            // Step 1: Create and populate the IntersectingRoutes temporary table
            String createTempTable = "CREATE TEMPORARY TABLE IntersectingRoutes (" +
                    "route_id_1 VARCHAR(255), " +
                    "route_id_2 VARCHAR(255), " +
                    "intersecting_stops TEXT);";

            String populateTempTable = "INSERT INTO IntersectingRoutes " +
                    "SELECT a.route_id AS route_id_1, b.route_id AS route_id_2, " +
                    "GROUP_CONCAT(DISTINCT a.stop_id ORDER BY a.stop_id) AS intersecting_stops " +
                    "FROM (SELECT DISTINCT t.route_id, st.stop_id FROM trips t " +
                    "JOIN stop_times st ON t.trip_id = st.trip_id " +
                    "JOIN routes r ON t.route_id = r.route_id " +
                    "JOIN stops s ON s.stop_id = st.stop_id " +
                    "WHERE (s.stop_lat BETWEEN 50.803792 AND 50.9) " +
                    "AND (s.stop_lon BETWEEN 5.640811 AND 5.739475) " +
                    "AND r.route_type = '3' AND r.route_short_name <> '797' " +
                    "AND r.route_short_name NOT LIKE '%trein%') a " +
                    "JOIN (SELECT DISTINCT t.route_id, st.stop_id FROM trips t " +
                    "JOIN stop_times st ON t.trip_id = st.trip_id " +
                    "JOIN routes r ON t.route_id = r.route_id " +
                    "JOIN stops s ON s.stop_id = st.stop_id " +
                    "WHERE (s.stop_lat BETWEEN 50.803792 AND 50.9) " +
                    "AND (s.stop_lon BETWEEN 5.640811 AND 5.739475) " +
                    "AND r.route_type = '3' AND r.route_short_name <> '797' " +
                    "AND r.route_short_name NOT LIKE '%trein%') b " +
                    "ON a.stop_id = b.stop_id AND a.route_id <> b.route_id " +
                    "GROUP BY a.route_id, b.route_id;";

            st.executeUpdate(createTempTable);
            st.executeUpdate(populateTempTable);

            // Step 2: Process the results from IntersectingRoutes
            ResultSet rs = st.executeQuery("SELECT * FROM IntersectingRoutes");
            while (rs.next()) {
                String intersectingStops = rs.getString("intersecting_stops");
                System.out.println("Route ID 1: " + rs.getString("route_id_1") +
                        ", Route ID 2: " + rs.getString("route_id_2") +
                        ", Intersecting Stops: " + intersectingStops);
            }
            String query1 = "SELECT intersecting_stops FROM IntersectingRoutes";
            Map<String, Integer> stopCounts = new HashMap<>();

            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery(query1);

            while (rs1.next()) {
                String[] stops = rs1.getString("intersecting_stops").split(",");
                for (String stop : stops) {
                    stopCounts.merge(stop, 1, Integer::sum);
                }
            }

            // Now stopCounts map contains all stops and their counts
            // Sorting and printing the counts
            stopCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(
                            entry -> System.out.println("Stop ID: " + entry.getKey() + ", Count: " + entry.getValue()));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
