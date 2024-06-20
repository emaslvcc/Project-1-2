package busjoris;

import java.time.LocalTime;

public class StopTime {
    private String stopID;
    private LocalTime time;

    public StopTime(String stopID, LocalTime time) {
        this.stopID = stopID;
        this.time = time;
    }

    public String getStopID() {
        return stopID;
    }

    public LocalTime getTime() {
        return time;
    }
}
