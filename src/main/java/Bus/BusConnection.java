package Bus;

import DataManagers.Node;
import Database.DatabaseUploader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusConnection {
    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    int fromBusStop;
    int toBusstop;

    public int getFromBusStop() {
        return fromBusStop;
    }

    public int getToBusstop() {
        return toBusstop;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getTripID() {
        return tripID;
    }

    String startingTime;
    String endTime;
    int tripID;
    int travelTime;
    String shortName;
    String longName;

    public BusConnection(int fromBusStop, int toBusstop, String startingTime, String endTime, int tripID,
            int travelTime, String shortName, String longName) {
        this.fromBusStop = fromBusStop;
        this.toBusstop = toBusstop;
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.tripID = tripID;
        this.travelTime = travelTime;
        this.shortName = shortName;
        this.longName = longName;
    }

    public List<Node> getRouteNodes() {
        System.out.println("Getting Nodes for the bus stops");
        String query = "SELECT s.stop_lat, s.stop_lon " +
                "FROM stop_times st " +
                "JOIN stops s ON st.stop_id = s.stop_id " +
                "WHERE st.trip_id = ? " +
                "AND st.stop_sequence <= (SELECT st2.stop_sequence FROM stop_times st2 WHERE st2.trip_id = ? AND stop_id = ?) "
                +
                "AND st.stop_sequence >= (SELECT st2.stop_sequence FROM stop_times st2 WHERE st2.trip_id = ? AND stop_id = ?);";

        List<Node> nodeList = new ArrayList<>();
        try (PreparedStatement statement = DatabaseUploader.myCon.prepareStatement(query)) {
            // Set the parameters for the PreparedStatement
            statement.setInt(1, tripID);
            statement.setInt(2, tripID);
            statement.setInt(3, toBusstop);
            statement.setInt(4, tripID);
            statement.setInt(5, fromBusStop);

            // Execute the query and process the ResultSet
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    nodeList.add(new Node(resultSet.getInt(1), resultSet.getDouble(1), resultSet.getDouble(2)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL query went wrong.");
        }
        return nodeList;
    }

    public int getTravelTime() {
        return travelTime;
    }

}
