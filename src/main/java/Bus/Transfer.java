package Bus;

public class Transfer {
    private final String stopID;
    private final String tripID;
    private final String busInfo;

    public String getStopID() {
        return stopID;
    }

    public String getTripID() {
        return tripID;
    }

    public String getBusInfo() {
        return busInfo;
    }

    public Transfer(String stopID, String tripID, String busInfo ){
        this.stopID = stopID;
        this.tripID = tripID;
        this.busInfo = busInfo;
    }
}
