package transfer;

import java.sql.*;

import Database.DatabaseConnection;

// pre-run
public class DataProcess {

    public static void main(String[] args) {
        String sqlCreateTT = """
                create temporary TABLE BusTransfer as
                SELECT
                  a.route_id AS route_id_1,
                    a.route_short_name AS route_short_name_1,
                  b.route_id AS route_id_2,
                  b.route_short_name AS route_short_name_2,
                  GROUP_CONCAT(DISTINCT a.stop_id ORDER BY a.stop_id) AS intersecting_stops
                FROM (
                  SELECT DISTINCT t.route_id, r.route_short_name, st.stop_id
                  FROM trips t
                  JOIN stop_times st ON t.trip_id = st.trip_id
                  JOIN routes r ON t.route_id = r.route_id
                  JOIN stops s ON s.stop_id = st.stop_id
                  WHERE (s.stostp_lat BETWEEN 50.803792 AND 50.9)
                    AND (s.stop_lon BETWEEN 5.640811 AND 5.739475)
                    AND r.route_type = '3'
                    AND r.route_short_name <> '797'
                    AND r.route_short_name NOT LIKE '%trein%'
                ) a
                JOIN (
                  SELECT DISTINCT t.route_id, r.route_short_name, st.stop_id
                  FROM trips t
                  JOIN stop_times st ON t.trip_id = st.trip_id
                  JOIN routes r ON t.route_id = r.route_id
                  JOIN stops s ON s.stop_id = st.stop_id
                  WHERE (s.stop_lat BETWEEN 50.803792 AND 50.9)
                    AND (s.stop_lon BETWEEN 5.640811 AND 5.739475)
                    AND r.route_type = '3'
                    AND r.route_short_name <> '797'
                    AND r.route_short_name NOT LIKE '%trein%'
                ) b ON a.stop_id = b.stop_id AND a.route_id <> b.route_id
                where a.route_id > b.route_id
                GROUP BY a.route_id, b.route_id
                ORDER BY a.route_id, b.route_id;
                    """;
        // SQL to fetch data; adjust according to actual table and column names
        String query = "SELECT * FROM BusTransfer st ";

        try (Connection con = DatabaseConnection.getConnection()) {
            Statement stmt0 = con.createStatement();
            stmt0.executeUpdate(sqlCreateTT);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Prepared statement for inserting into transferStops
            String insertQuery = "INSERT INTO transferStops (route_id_1, route_short_name_1, route_id_2, route_short_name_2, intersecting_stops) "
                    +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                while (rs.next()) {
                    String routeId1 = rs.getString("route_id_1");
                    String routeShortName1 = rs.getString("route_short_name_1");
                    String routeId2 = rs.getString("route_id_2");
                    String routeShortName2 = rs.getString("route_short_name_2");
                    String[] stops = rs.getString("intersecting_stops").split(",");

                    for (String stop : stops) {
                        insertStmt.setString(1, routeId1);
                        insertStmt.setString(2, routeShortName1);
                        insertStmt.setString(3, routeId2);
                        insertStmt.setString(4, routeShortName2);
                        insertStmt.setString(5, stop.trim()); // Trim to remove any extra spaces
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
