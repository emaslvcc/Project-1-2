package Bus;

import java.sql.*;

import Database.DatabaseConnection;

/**
 * Represents information about a specific bus trip.
 */
public class TripInfo {
    String routeId;
    String busNumber;
    String busName;
    String tripId;
    String startStopId;
    String endStopId;
    String startDepartureTime;
    String endArrivalTime;
    int tripTime;
    double distanceToFirstBusstop;

    /**
     * Constructs a new trip with the specified details.
     * 
     * @param routeId            the ID of the route
     * @param busNumber          the number of the bus
     * @param busName            the name of the bus
     * @param tripId             the ID of the trip
     * @param startStopId        the ID of the start stop
     * @param endStopId          the ID of the end stop
     * @param startDepartureTime the departure time at the start stop
     * @param endArrivalTime     the arrival time at the end stop
     * @param tripTime           the duration of the trip in minutes
     */
    public TripInfo(String routeId, String busNumber, String busName, String tripId, String startStopId,
            String endStopId, String startDepartureTime, String endArrivalTime, int tripTime) {
        this.routeId = routeId;
        this.busNumber = busNumber;
        this.busName = busName;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startDepartureTime = startDepartureTime;
        this.endArrivalTime = endArrivalTime;
        this.tripTime = tripTime;
    }

    public TripInfo(String routeId, String busNumber, String tripId, String startStopId,
            String endStopId, String startDepartureTime, String endArrivalTime, int tripTime) {
        this.routeId = routeId;
        this.busNumber = busNumber;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startDepartureTime = startDepartureTime;
        this.endArrivalTime = endArrivalTime;
        this.tripTime = tripTime;
    }

    public TripInfo(String routeId, String busNumber, String tripId, String startStopId,
            String endStopId, String startDepartureTime, String endArrivalTime, int tripTime,
            double distanceToFirstBusstop) {
        this.routeId = routeId;
        this.busNumber = busNumber;
        this.tripId = tripId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startDepartureTime = startDepartureTime;
        this.endArrivalTime = endArrivalTime;
        this.tripTime = tripTime;
        this.distanceToFirstBusstop = distanceToFirstBusstop;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getBusName() {
        return busName;
    }

    public int getTripTime() {
        return tripTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStartDepartureTime() {
        return startDepartureTime;
    }

    public String getEndArrivalTime() {
        return endArrivalTime;
    }

    public String getStartStopId() {
        return startStopId;
    }

    public String getEndStopId() {
        return endStopId;
    }

    public String getColor() throws SQLException {
        String sql = """
                SELECT route_color
                from routes r
                where r.route_id = ?;
                    """;
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, this.routeId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            return rs.getString(1);
        }
        return "blue";

    }

    public long getArrTimeInMs() {
        Time timeObject = Time.valueOf(this.getEndArrivalTime());
        return timeObject.getTime();
    }

    @Override
    public String toString() {
        return "Route ID: " + routeId +
                ", Bus Number: " + busNumber +
                ", Bus Name: " + busName +
                ", Trip ID: " + tripId +
                ", Start stop ID: " + startStopId +
                ", End stop ID: " + endStopId +
                ", Start Departure Time: " + startDepartureTime +
                ", End Arrival Time: " + endArrivalTime +
                ", Trip Time: " + tripTime + " minutes";
    }

}
