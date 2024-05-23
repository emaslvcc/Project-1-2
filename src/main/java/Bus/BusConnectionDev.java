package Bus;

import java.sql.*;
import java.util.*;

import DataManagers.PostCode;
import Database.DatabaseConnection;

class RouteStopInfo {
    String routeId;
    String startStopId;
    String endStopId;

    RouteStopInfo(String routeId, String startStopId, String endStopId) {
        this.routeId = routeId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
    }
}

class TripInfo {
    String routeId;
    String tripId;
    String startStopId;
    String endStopId;
    String startDepartureTime;
    String endArrivalTime;
    int tripTime;

    public TripInfo(String routeId, String tripId, String startStopId,
            String endStopId, String startDepartureTime, String endArrivalTime, int tripTime) {
        this.routeId = routeId;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startDepartureTime = startDepartureTime;
        this.endArrivalTime = endArrivalTime;
        this.tripTime = tripTime;
    }

    // Getter for tripTime
    public int getTripTime() {
        return tripTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTripId() {
        return tripId;
    }

    // Getter for endArrivalTime
    public String getArrivalTime() {
        return endArrivalTime;
    }

    public String getStartStopId() {
        return startStopId;
    }

    public String getEndStopId() {
        return endStopId;
    }

    @Override
    public String toString() {
        return "Route ID: " + routeId +
                ", Trip ID: " + tripId +
                ", Start stop ID: " + startStopId +
                ", End stop ID: " + endStopId +
                ", Start Departure Time: " + startDepartureTime +
                ", End Arrival Time: " + endArrivalTime +
                ", Trip Time: " + tripTime + " minutes";
    }

}

public class BusConnectionDev {
    PostCode startPostCode;
    PostCode endPostCode;

    double startLat = startPostCode.getLatitude();
    double startLon = startPostCode.getLongitude();
    double endLat = endPostCode.getLatitude();
    double endLon = endPostCode.getLongitude();

    public static void main(String[] args) {

        try {
            Connection conn = DatabaseConnection.getConnection();
            setupNearestStops(conn, 50.849782, 5.705090, 50.855008, 5.666684); // Start and end coordinates
            findPotentialRoutes(conn);
            List<RouteStopInfo> routes = findRouteBusStops(conn);
            TripInfo bestTrip = null; // Start with no best trip found

            for (RouteStopInfo route : routes) {
                List<TripInfo> tripsForRoute = printNextDepartureAndArrival(conn,
                        route.routeId, route.startStopId,
                        route.endStopId);
                for (TripInfo trip : tripsForRoute) {
                    // Update the best trip based on trip time and arrival time.
                    if (bestTrip == null || trip.getTripTime() < bestTrip.getTripTime() ||
                            (trip.getTripTime() == bestTrip.getTripTime()
                                    && trip.getArrivalTime().compareTo(bestTrip.getArrivalTime()) < 0)) {
                        bestTrip = trip; // Found a new best trip.
                    }
                }
            }

            if (bestTrip != null) {
                System.out.println("Best Trip: " + bestTrip);
                createAndQueryShapes(conn, bestTrip);
                System.out.println("========================================");
                queryStopsBetween(conn, bestTrip.getTripId(), bestTrip.getStartStopId(), bestTrip.getEndStopId());

            } else {
                System.out.println("No upcoming trips found.");
            }

        } catch (

        SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setupNearestStops(Connection conn, double startLat, double startLon, double endLat,
            double endLon) throws SQLException {
        String createNearestStartStops = "CREATE TEMPORARY TABLE nearest_start_stops AS " +
                "SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance "
                +
                "FROM stops ORDER BY distance LIMIT 10;";
        String createNearestEndStops = "CREATE TEMPORARY TABLE nearest_end_stops AS " +
                "SELECT stop_id, stop_name, ST_Distance_Sphere(point(?, ?), point(stops.stop_lon, stops.stop_lat)) AS distance "
                +
                "FROM stops ORDER BY distance LIMIT 10;";

        try (PreparedStatement pstmt1 = conn.prepareStatement(createNearestStartStops);
                PreparedStatement pstmt2 = conn.prepareStatement(createNearestEndStops)) {
            pstmt1.setDouble(1, startLon);
            pstmt1.setDouble(2, startLat);
            pstmt2.setDouble(1, endLon);
            pstmt2.setDouble(2, endLat);
            pstmt1.executeUpdate();
            pstmt2.executeUpdate();
        }
    }

    public static void findPotentialRoutes(Connection conn) throws SQLException {
        String sql = "CREATE TEMPORARY TABLE potential_routes AS " +
                "SELECT st1.trip_id, t.route_id, st1.stop_id AS start_stop_id, st2.stop_id AS end_stop_id, " +
                "nss.distance AS start_distance, nes.distance AS end_distance " +
                "FROM stop_times st1 " +
                "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence " +
                "JOIN trips t ON st1.trip_id = t.trip_id " +
                "JOIN nearest_start_stops nss ON st1.stop_id = nss.stop_id " +
                "JOIN nearest_end_stops nes ON st2.stop_id = nes.stop_id;";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static List<RouteStopInfo> findRouteBusStops(Connection conn) throws SQLException {
        List<RouteStopInfo> routes = new ArrayList<>();
        String setGroupConcatMaxLen = "SET SESSION group_concat_max_len = 1000000;";
        String sql = "SELECT route_id, start_stop_id, end_stop_id FROM route_bus_stops;";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(setGroupConcatMaxLen); // Extend group_concat_max_len for large datasets
            stmt.execute("CREATE TEMPORARY TABLE route_bus_stops AS " +
                    "SELECT route_id, MIN(start_distance + end_distance) AS total_distance, " +
                    "SUBSTRING_INDEX(GROUP_CONCAT(start_stop_id ORDER BY (start_distance + end_distance)), ',', 1) AS start_stop_id, "
                    +
                    "SUBSTRING_INDEX(GROUP_CONCAT(end_stop_id ORDER BY (start_distance + end_distance)), ',', 1) AS end_stop_id "
                    +
                    "FROM potential_routes GROUP BY route_id ORDER BY total_distance;");

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

    private static List<TripInfo> printNextDepartureAndArrival(Connection conn, String routeId, String startStopId,
            String endStopId) throws SQLException {
        String sql = "SELECT t.route_id, st1.trip_id, st1.departure_time AS start_departure_time, st2.arrival_time AS end_arrival_time, "
                + "TIMESTAMPDIFF(MINUTE, st1.departure_time, st2.arrival_time) AS trip_time " // Calculating trip time
                                                                                              // in minutes for clarity
                + "FROM stop_times st1 "
                + "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence "
                + "JOIN trips t ON t.trip_id = st1.trip_id " // Joining with trips to include route_id in the query
                + "WHERE st1.stop_id = ? AND st2.stop_id = ? AND t.route_id = ? AND st1.departure_time >= CURRENT_TIME() "
                + "ORDER BY st1.departure_time "
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

    private static void createAndQueryShapes(Connection conn, TripInfo bestTrip) throws SQLException {
        createTemporaryTableNearestStartShape(conn, bestTrip.getRouteId(), bestTrip.getStartStopId());
        createTemporaryTableNearestEndShape(conn, bestTrip.getRouteId(), bestTrip.getEndStopId());
        createTemporaryTableCommonShapeId(conn);
        queryShapeDetails(conn);
    }

    private static void createTemporaryTableNearestStartShape(Connection conn, String routeId, String startStopId)
            throws SQLException {
        String sql = "CREATE TEMPORARY TABLE NearestStartShape AS " +
                "SELECT t.route_id, stp.stop_id, s.shape_id, s.shape_pt_lat, s.shape_pt_lon, s.shape_pt_sequence AS start_shape_sequence,"
                +
                "MIN(ST_Distance_Sphere(point(stp.stop_lon, stp.stop_lat), point(s.shape_pt_lon, s.shape_pt_lat))) AS min_distance "
                +
                "FROM stops stp " +
                "JOIN trips t ON t.route_id = ? " +
                "JOIN shapes s ON t.shape_id = s.shape_id " +
                "WHERE stp.stop_id = ? " +
                "GROUP BY s.shape_id, s.shape_pt_sequence " +
                "ORDER BY min_distance ASC " +
                "LIMIT 3;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, routeId);
            pstmt.setString(2, startStopId);
            pstmt.executeUpdate();
        }
    }

    private static void createTemporaryTableNearestEndShape(Connection conn, String routeId, String endStopId)
            throws SQLException {
        String sql = "CREATE TEMPORARY TABLE NearestEndShape AS " +
                "SELECT t.route_id, stp.stop_id, s.shape_id, s.shape_pt_lat, s.shape_pt_lon, s.shape_pt_sequence AS end_shape_sequence, "
                +
                "MIN(ST_Distance_Sphere(point(stp.stop_lon, stp.stop_lat), point(s.shape_pt_lon, s.shape_pt_lat))) AS min_distance "
                +
                "FROM stops stp " +
                "JOIN trips t ON t.route_id = ? " +
                "JOIN shapes s ON t.shape_id = s.shape_id " +
                "WHERE stp.stop_id = ? " +
                "GROUP BY s.shape_id, s.shape_pt_sequence " +
                "ORDER BY min_distance ASC " +
                "LIMIT 3;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, routeId);
            pstmt.setString(2, endStopId);
            pstmt.executeUpdate();
        }
    }

    private static void createTemporaryTableCommonShapeId(Connection conn) throws SQLException {
        String sql = "CREATE TEMPORARY TABLE CommonShapeId AS " +
                "SELECT DISTINCT ns.shape_id " +
                "FROM NearestStartShape ns " +
                "JOIN NearestEndShape ne ON ns.shape_id = ne.shape_id " +
                "LIMIT 1;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
    }

    private static void queryShapeDetails(Connection conn) throws SQLException {
        String sql = "SELECT DISTINCT " +
                "ns.route_id, " +
                "ns.shape_id, " +
                "sh.shape_pt_sequence, " +
                "sh.shape_pt_lat, " +
                "sh.shape_pt_lon " +
                "FROM NearestStartShape ns " +
                "JOIN NearestEndShape ne ON ns.route_id = ne.route_id AND ns.shape_id = ne.shape_id " +
                "JOIN shapes sh ON ns.shape_id = sh.shape_id " +
                "JOIN CommonShapeId csi ON csi.shape_id = ns.shape_id " + // Filter by the common shape_id
                "WHERE (sh.shape_pt_sequence BETWEEN ns.start_shape_sequence AND ne.end_shape_sequence " +
                "OR sh.shape_pt_sequence BETWEEN ne.end_shape_sequence AND ns.start_shape_sequence) " +
                "ORDER BY sh.shape_pt_sequence;";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String routeId = rs.getString("route_id");
                String shapeId = rs.getString("shape_id");
                int shapePtSequence = rs.getInt("shape_pt_sequence");
                double shapePtLat = rs.getDouble("shape_pt_lat");
                double shapePtLon = rs.getDouble("shape_pt_lon");

                System.out.println("Route ID: " + routeId +
                        ", Shape ID: " + shapeId +
                        ", Shape Pt Sequence: " + shapePtSequence +
                        ", Latitude: " + shapePtLat +
                        ", Longitude: " + shapePtLon);
            }
            System.out.println("Query completed.");

        }
    }

    private static void queryStopsBetween(Connection conn, String tripId, String startStopId, String endStopId) {
        String sql = "SELECT st.trip_id, st.stop_id, s.stop_name, st.stop_sequence, s.stop_lat, s.stop_lon " +
                "FROM stop_times st " +
                "JOIN stops s ON st.stop_id = s.stop_id " +
                "WHERE st.trip_id = ? AND " +
                "st.stop_sequence > (SELECT stop_sequence FROM stop_times WHERE trip_id = ? AND stop_id = ?) AND " +
                "st.stop_sequence < (SELECT stop_sequence FROM stop_times WHERE trip_id = ? AND stop_id = ?) " +
                "ORDER BY st.stop_sequence;";

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
                int stopSequence = rs.getInt("stop_sequence");
                double stopLat = rs.getDouble("stop_lat");
                double stopLon = rs.getDouble("stop_lon");

                System.out.println("Trip ID: " + tripId + ", Stop ID: " + stopId + ", Stop Name: " + stopName +
                        ", Stop Sequence: " + stopSequence + ", Latitude: " + stopLat + ", Longitude: " + stopLon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}