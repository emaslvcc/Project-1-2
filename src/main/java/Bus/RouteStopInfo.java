package Bus;

/**
 * Represents information about a bus route stop.
 */
public class RouteStopInfo {
    String routeId;
    String startStopId;
    String endStopId;

    /**
     * Constructs a new route with the specified route ID, start stop ID, and end
     * stop ID.
     * 
     * @param routeId     the ID of the route
     * @param startStopId the ID of the start stop
     * @param endStopId   the ID of the end stop
     */
    RouteStopInfo(String routeId, String startStopId, String endStopId) {
        this.routeId = routeId;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
    }
}
