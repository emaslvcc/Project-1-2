package Bus;

import java.sql.Time;
import java.util.List;

import Calculators.DistanceCalculatorHaversine;
import Calculators.TimeCalculator;
import DataManagers.LogicManager;
import DataManagers.Node;

public class test {
    public static void main(String[] args) {
    //     int timeToAdd = 15; // Minutes to add

    //     // Assuming you start with a Time object
    //     Time earlierTime = Time.valueOf("10:20:00");

    //     // Convert the timeToAdd to milliseconds
    //     long additionalTimeInMs = timeToAdd * 60 * 1000;

    //     // Create a new Time object with the added time
    //     Time newTime = new Time(earlierTime.getTime() + additionalTimeInMs);

    //     // Print the result
    //     System.out.println("New time: " + newTime.toString());
    // }
   double distanceToBusstop = DistanceCalculatorHaversine.calculate(50.838603939296895, 5.7345234466946255,
   50.8395643277231, 5.7345039492092);
   System.out.println(distanceToBusstop);
    }
}
