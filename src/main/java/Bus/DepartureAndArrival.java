package Bus;

import java.time.LocalTime;

public class DepartureAndArrival {
    LocalTime dep;
    LocalTime arrival;

    public DepartureAndArrival(LocalTime dep, LocalTime arrival) {
        this.dep = dep;
        this.arrival = arrival;
    }

    public LocalTime getDep() {
        return dep;
    }

    public LocalTime getArrival() {
        return arrival;
    }
}
