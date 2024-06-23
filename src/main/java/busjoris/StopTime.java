package busjoris;

import java.time.LocalTime;

public class StopTime {
    private String stopID;
    private LocalTime time;
    private LocalTime timeStart;

    public StopTime(String stopID, LocalTime time) {
        this.stopID = stopID;
        this.time = time;
    }
    public StopTime(String stopID, LocalTime time, LocalTime time2) {
        this.stopID = stopID;
        this.time = time;
        this.timeStart = time2;
    }

    public LocalTime getStartTime() {
        return timeStart;
    }

    public String getStopID() {
        return stopID;
    }

    public LocalTime getTime() {
        return time;
    }
}

