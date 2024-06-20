package Bus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DataManagers.LogicManager;
import DataManagers.Node;
import Database.DatabaseConnection;

public class insertData {

    public static void main(String[] args) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        calculateAndStoreDistances(connection);
    }

    public static void calculateAndStoreDistances(Connection connection) throws SQLException {
        // Fetch all zip codes
        PreparedStatement zipPstmt = connection.prepareStatement(
                "SELECT zipcode, latitude, longitude FROM post_codes");
        ResultSet zipRs = zipPstmt.executeQuery();

        while (zipRs.next()) {
            double zipLat = zipRs.getDouble("latitude");
            double zipLon = zipRs.getDouble("longitude");

            // Calculate 20 nearest bus stops for this zipcode
            List<Node> nearestStops = getNearestBusStops(connection, zipLat, zipLon);

            // For each bus stop, calculate the walking distance from the zipcode
            for (Node stop : nearestStops) {
                try {
                    System.out.println("lat" + stop.getLat());
                    List<Node> shortestPath = LogicManager.calculateRouteByCoordinates(zipLat, zipLon, stop.getLat(),
                            stop.getLon(), "walk");

                    double distanceToStop = LogicManager.calculateDistance(shortestPath);
                    System.out.println(zipLat + " " + zipLon + " " + stop.getLat() + " " + stop.getLon());
                    // Store the result in the database
                    storeDistance(connection, zipLat, zipLon, stop.getLat(), stop.getLon(), distanceToStop);

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        }
    }

    private static List<Node> getNearestBusStops(Connection connection, double lat, double lon)
            throws SQLException {
        List<Node> stops = new ArrayList<>();
        PreparedStatement pstmtStop = connection.prepareStatement(
                "SELECT stop_id, stop_name, stop_lat, stop_lon, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance "
                        +
                        "FROM stops " +
                        "ORDER BY distance " +
                        "LIMIT 20");
        pstmtStop.setDouble(1, lon);
        pstmtStop.setDouble(2, lat);
        ResultSet rs = pstmtStop.executeQuery();

        while (rs.next()) {
            stops.add(new Node(
                    rs.getInt("stop_id"),
                    rs.getDouble("stop_lat"),
                    rs.getDouble("stop_lon")

            ));
        }
        return stops;
    }

    private static void storeDistance(Connection connection, double startLat, double startLon, double endLat,
            double endLon,
            double distance) throws SQLException {

        PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO distanceToBusstop (start_latitude, start_longitude, end_latitude, end_longitude, distance) "
                        +
                        "VALUES (?, ?, ?, ?, ?)");
        System.out.println(startLat + " " + startLon + " " + endLat + " " + endLon + " " + distance);

        pstmt.setDouble(1, startLat);
        pstmt.setDouble(2, startLon);
        pstmt.setDouble(3, endLat);
        pstmt.setDouble(4, endLon);
        pstmt.setInt(5, (int) Math.round(distance)); // Store only the integer part of the distance
        pstmt.executeUpdate();
    }
}
