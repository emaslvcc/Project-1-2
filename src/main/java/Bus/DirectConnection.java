package Bus;

import DataManagers.PostCode;
import Database.DatabaseUploader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class DirectConnection {

    private static int tripId;
    
    private static boolean checkConnection(int start_bus_stop_id, int end_bus_stop_id) {

        if(start_bus_stop_id == end_bus_stop_id){
            System.out.println("The SAME STOPS");
            return false;
        }

        String query = "SELECT DISTINCT routes.route_id, routes.route_short_name, routes.route_long_name, trips.trip_id " +
                       "FROM routes " +
                       "JOIN trips ON routes.route_id = trips.route_id " +
                       "JOIN stop_times ON trips.trip_id = stop_times.trip_id " +
                       "WHERE stop_times.stop_id = ? " +
                       "AND trips.trip_id IN (SELECT trip_id FROM stop_times WHERE stop_id = ?)";
        
        try (PreparedStatement statement = DatabaseUploader.myCon.prepareStatement(query)) {
            // Set the parameters
            statement.setInt(1, start_bus_stop_id);
            statement.setInt(2, end_bus_stop_id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                // Check if there are any results
                tripId = resultSet.getInt("trip_id");
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("SQL query went wrong.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL query went wrong.");
        }
        return false;
    }

    private int[] getClosestStops(int range, double latitude, double longitude) {
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
                    int stopId = resultSet.getInt("stop_id");  // Retrieve stop_id from ResultSet
                    stopIds.add(stopId);  // Add stop_id to ArrayList
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

        return stopIdArray;  // Return the array of stop_ids
    }

    public int[] bestWay(PostCode startCode, PostCode endCode, int range){

        int[] startBusStops = getClosestStops(range, startCode.latitude, startCode.longitude);
        int[] endBusStops = getClosestStops(range, endCode.latitude, endCode.longitude);

        int[] finalStops = new int[3];
        int[][] dp = new int [startBusStops.length][endBusStops.length];

        int minTime = Integer.MAX_VALUE;

        // Initialize DP table with high values
        for (int i = 0; i < startBusStops.length; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
            for (int j = 0; j < endBusStops.length; j++) {
                int time = findTime(startBusStops[i], endBusStops[j]);
                if (time != Integer.MAX_VALUE) {
                    dp[i][j] = time;
                    if (time < minTime) {
                        minTime = time;
                        finalStops[0] = startBusStops[i];
                        finalStops[1] = endBusStops[j];
                        finalStops[2] = minTime;
                    }
                }
            }
        }

        return finalStops;
    }

    private int findTime(int start_bus_stop_id, int end_bus_stop_id){

        if(!checkConnection(start_bus_stop_id, end_bus_stop_id)){
            return Integer.MAX_VALUE;
        } else {
            String query = "SELECT A.stop_id AS start_stop_id, B.stop_id AS end_stop_id,A.departure_time AS departure_time_start,"+
                    " B.arrival_time AS arrival_time_end, " +
                    "TIME_TO_SEC(TIMEDIFF(B.arrival_time, A.departure_time)) / 60 AS time_difference_minutes" +
                    "FROM stop_times A JOIN stop_times B ON A.trip_id = B.trip_id" +
                    "WHERE A.stop_id = ? AND B.stop_id = ? AND A.trip_id = ?";

            try (PreparedStatement statement = DatabaseUploader.myCon.prepareStatement(query)) {
                // Set the parameters for the PreparedStatement
                statement.setInt(1, start_bus_stop_id);
                statement.setInt(2, end_bus_stop_id);
                statement.setInt(3, tripId);

                // Execute the query and process the ResultSet
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        return resultSet.getInt("time_difference_minutes");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("SQL query went wrong.");
            }
            return 0;
        }
    }
}
