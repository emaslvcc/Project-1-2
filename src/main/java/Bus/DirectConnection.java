package Bus;

import DataManagers.PostCode;
import Database.DatabaseUploader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DirectConnection {

    public static List<BusConnection> getConnections(int start_bus_stop_id, int end_bus_stop_id) {

        if (start_bus_stop_id == end_bus_stop_id) {
            System.out.println("The SAME STOPS");
            return new ArrayList<BusConnection>();
        }
        System.out.println(
                "checking if direct connection between " + start_bus_stop_id + " " + end_bus_stop_id + " exists");

        String query = "SELECT DISTINCT r.route_id, r.route_short_name, r.route_long_name, t.trip_id, t.direction_id, st2.departure_time, st.arrival_time, "
                +
                "TIME_TO_SEC(TIMEDIFF(st.arrival_time, st2.departure_time)) AS time_diff " +
                "FROM routes r " +
                "JOIN trips t ON r.route_id = t.route_id " +
                "JOIN stop_times st ON t.trip_id = st.trip_id " +
                "JOIN stop_times st2 ON t.trip_id = st2.trip_id AND st2.stop_id = ? " +
                "WHERE st.stop_id = ? " +
                "AND r.route_id IN ( " +
                "    SELECT DISTINCT r2.route_id " +
                "    FROM routes r2 " +
                "    JOIN trips t2 ON r2.route_id = t2.route_id " +
                "    JOIN stop_times st2 ON t2.trip_id = st2.trip_id " +
                "    WHERE st2.stop_id = ? AND t.direction_id = t2.direction_id " +
                ") " +
                "AND st2.departure_time >= CURRENT_TIME " +
                "ORDER BY st2.departure_time;";

        List<BusConnection> busConnections = new LinkedList<>();

        try (PreparedStatement statement = DatabaseUploader.myCon.prepareStatement(query)) {
            statement.setQueryTimeout(10);
            // Set the parameters
            statement.setString(1, String.valueOf(start_bus_stop_id));
            statement.setString(2, String.valueOf(end_bus_stop_id));
            statement.setString(3, String.valueOf(start_bus_stop_id));
            System.out.println("executing query");

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Got result");
                // Check if there are any results
                while (resultSet.next()) {
                    busConnections.add(new BusConnection(start_bus_stop_id, end_bus_stop_id,
                            resultSet.getString("departure_time"), resultSet.getString("arrival_time"),
                            resultSet.getInt("trip_id"), resultSet.getInt("time_diff"),
                            resultSet.getString("route_short_name"), resultSet.getString("route_long_name")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("SQL query went wrong.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL query went wrong.");
        }

        if (busConnections.size() == 0) {
            System.out.println("There is no direct connection");
            return busConnections;
        } else {
            System.out.println(busConnections.size() + " trips have been found");
            return busConnections;
        }
    }

    private int[] getClosestStops(int range, double latitude, double longitude) {
        System.out.println("Getting nearby bus stops");
        // Corrected and formatted SQL query
        String query = "SELECT stop_id, stop_name, stop_lat, stop_lon, distance " +
                "FROM (SELECT stop_id, stop_name, stop_lat, stop_lon, " +
                "ST_Distance_Sphere(POINT(stop_lat, stop_lon), POINT(?, ?)) as distance " +
                "FROM stops) as subquery " +
                "WHERE distance <= ? " +
                "ORDER BY distance ASC;";

        // ArrayList to collect stop_ids
        ArrayList<Integer> stopIds = new ArrayList<>();

        // Try-with-resources statement for managing resources
        try (PreparedStatement statement = DatabaseUploader.myCon.prepareStatement(query)) {
            // Set the parameters for the PreparedStatement
            statement.setDouble(1, latitude);
            statement.setDouble(2, longitude);
            statement.setInt(3, range);

            // Execute the query and process the ResultSet
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int stopId = resultSet.getInt("stop_id"); // Retrieve stop_id from ResultSet
                    stopIds.add(stopId); // Add stop_id to ArrayList
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL query went wrong.");
        }

        // Convert ArrayList to an array
        int[] stopIdArray = new int[stopIds.size()];
        for (int i = 0; i < stopIdArray.length; i++) {
            stopIdArray[i] = stopIds.get(i);
        }

        return stopIdArray; // Return the array of stop_ids
    }

    public BusConnection bestWay(PostCode startCode, PostCode endCode, int range) {
        int[] startBusStops = getClosestStops(range, startCode.latitude, startCode.longitude);
        int[] endBusStops = getClosestStops(range, endCode.latitude, endCode.longitude);

        BusConnection currMin = new BusConnection(-1, -1, "", "", 0, Integer.MAX_VALUE, "DNE", "Does not exist");

        // Initialize DP table with high values
        // for (int i = 0; i < startBusStops.length; i++) {
        // for (int j = 0; j < endBusStops.length; j++) {
        // List<BusConnection> con = getConnections(startBusStops[i], endBusStops[j]);
        // if(!con.isEmpty() && con.getFirst().travelTime < currMin.travelTime &&
        // con.getFirst().travelTime > 0){
        // currMin = con.getFirst();
        // }
        // }
        // }
        System.out.println(
                currMin.getStartingTime() + currMin.getEndTime() + currMin.getLongName() + currMin.getShortName());
        //GUI.createMap.drawPath(currMin.getRouteNodes());
        return currMin;
    }

    // private int findTime(int start_bus_stop_id, int end_bus_stop_id){
    //
    // if(getConnections(start_bus_stop_id, end_bus_stop_id).isEmpty()){
    // return Integer.MAX_VALUE;
    // } else {
    // System.out.println("querying time");
    // String query = "SELECT A.stop_id AS start_stop_id, B.stop_id AS
    // end_stop_id,A.departure_time AS departure_time_start,"+
    // " B.arrival_time AS arrival_time_end, " +
    // "TIME_TO_SEC(TIMEDIFF(B.arrival_time, A.departure_time)) / 60 AS
    // time_difference_minutes" +
    // "FROM stop_times A JOIN stop_times B ON A.trip_id = B.trip_id" +
    // "WHERE A.stop_id = ? AND B.stop_id = ? AND A.trip_id = ?";
    //
    // try (PreparedStatement statement =
    // DatabaseUploader.myCon.prepareStatement(query)) {
    // // Set the parameters for the PreparedStatement
    // statement.setInt(1, start_bus_stop_id);
    // statement.setInt(2, end_bus_stop_id);
    // statement.setInt(3, tripId);
    //
    // // Execute the query and process the ResultSet
    // try (ResultSet resultSet = statement.executeQuery()) {
    // while (resultSet.next()) {
    // return resultSet.getInt("time_difference_minutes");
    // }
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // System.out.println("SQL query went wrong.");
    // }
    // return 0;
    // }
}
