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
            System.out.println(zipLat + " " + zipLon);

            // Calculate 20 nearest bus stops for this zipcode
            List<stopNode> nearestStops = getNearestBusStops(connection, zipLat, zipLon);

            // For each bus stop, calculate the walking distance from the zipcode
            for (stopNode stop : nearestStops) {
                try {
                    System.out.println("lat" + stop.getLat());
                    List<Node> shortestPath = LogicManager.calculateRouteByCoordinates(zipLat, zipLon, stop.getLat(),
                            stop.getLon(), "walk");

                    double distanceToStop = LogicManager.calculateDistance(shortestPath);

                    // Store the result in the database
                    storeDistance(connection, zipLat, zipLon, stop.getLat(), stop.getLon(), distanceToStop);

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
    }

    private static List<stopNode> getNearestBusStops(Connection connection, double lat, double lon)
            throws SQLException {
        List<stopNode> stops = new ArrayList<>();
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
            stops.add(new stopNode(
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
        pstmt.setDouble(1, startLat);
        pstmt.setDouble(2, startLon);
        pstmt.setDouble(3, endLat);
        pstmt.setDouble(4, endLon);
        pstmt.setInt(5, (int) Math.round(distance)); // Store only the integer part of the distance
        pstmt.executeUpdate();
    }
}

class stopNode {
    private double lat;
    private double lon;
    private int id;

    public stopNode(int id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;

    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getId() {
        return id;
    }
}
