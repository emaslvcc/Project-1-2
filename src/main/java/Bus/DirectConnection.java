package Bus;

import Database.DatabaseUploader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DirectConnection {
    
    public static boolean checkConnection(int start_bus_stop_id, int end_bus_stop_id) {
        String query = "SELECT DISTINCT routes.route_id, routes.route_short_name, routes.route_long_name " +
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
}
