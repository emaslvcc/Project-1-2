package Calculators;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import DataManagers.LogicManager;
import DataManagers.Node;
import Bus.DistanceCache;

/**
 * The GenericCalculator class provides a base for calculating walking and
 * cycling times.
 */
public abstract class TimeCalculator {

    double walkingTime;
    double cyclingTime;

    /**
     * Calculates the time required to cover a certain distance at a given speed.
     * 
     * @param distance The distance to cover.
     * @param speed    The speed at which to cover the distance.
     * @return The time required to cover the distance at the given speed.
     */
    protected double calculateTime(double distance, double speed) {
        if (distance < 0) {
            distance = distance * (-1);
        }
        return distance / speed;
    }

    /**
     * Retrieves the walking time calculated by the calculator.
     * 
     * @return The walking time in minutes.
     */
    public double getWalkingTime() {
        return this.walkingTime;
    }

    /**
     * Retrieves the cycling time calculated by the calculator.
     * 
     * @return The cycling time in minutes.
     */
    public double getCyclingTime() {
        return this.cyclingTime;
    }

    public static Time getwalkingTime(double startLat, double startLon, double endLat, double endLon) {
        double distanceToStartBusstop = calculateDistanceIfNotCached(startLat, startLon, endLat, endLon);
        TimeCalculator timeCalc = new AverageTimeCalculator(distanceToStartBusstop);
        int time = (int) (Math.round(timeCalc.getWalkingTime()));
        long timeInLong = time * 60 * 1000;
        Time walkingTime = new Time(timeInLong);
        return walkingTime;
    }

    public static int calculateTripTime(String startTime, String endTime) {

        // Convert the String to a Time object
        Time startTime1 = Time.valueOf(startTime);
        Time startTime2 = Time.valueOf(endTime);

        // Calculate the difference in milliseconds
        long differenceInMilliseconds = Math.abs(startTime1.getTime() - startTime2.getTime());

        // Convert milliseconds to minutes
        int differenceInMinutes = (int) differenceInMilliseconds / (1000 * 60);

        return differenceInMinutes;
    }

    public static Time calculateTime(double startLat, double startLon, double endLat, double endLon) {

        double distanceToStartBusstop = calculateDistanceIfNotCached(startLat, startLon, endLat, endLon);
        Time baseTime = getCurrentTime();
        TimeCalculator timeCalc = new AverageTimeCalculator(distanceToStartBusstop);
        int time = (int) (Math.round(timeCalc.getWalkingTime()));
        long baseTimeInMs = baseTime.getTime();
        long additionalTimeInMs = time * 60 * 1000;
        Time newTime = new Time(baseTimeInMs + additionalTimeInMs);
        return newTime;
    }

    public static double calculateDistanceIfNotCached(double startLat, double startLon, double endLat, double endLon) {
        DistanceCache distanceCache = new DistanceCache();

        Double cachedDistance = DistanceCache.getDistance(startLat, startLon, endLat, endLon);
        if (cachedDistance != null) {
            return cachedDistance;
        } else {
            double distance = DistanceCalculatorHaversine.calculate(startLat, startLon, endLat, endLon);
            if (distance > 0.3) {
                List<Node> path = LogicManager.calculateRouteByCoordinates(startLat, startLon, endLat, endLon, "walk");
                distance = LogicManager.calculateDistance(path);
            }
            distanceCache.putDistance(startLat, startLon, endLat, endLon, distance);
            return distance;
        }
    }

    public static Time getCurrentTime() {
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.SECOND, 0);
        // calendar.set(Calendar.MILLISECOND, 0);
        // Time currentTime = new Time(calendar.getTimeInMillis());
        Time currentTime = Time.valueOf("10:20:00");
        return currentTime;
    }

    /**
     * Adds minutes to a given Time object.
     * 
     * @param time         The Time object.
     * @param minutesToAdd The number of minutes to add.
     * @return A new Time object representing the time after adding the specified
     *         minutes.
     */
    public static Time addMinutesToTime(Time time, int minutesToAdd) {
        // Get a calendar instance to perform the addition
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        // Add the minutes
        calendar.add(Calendar.MINUTE, minutesToAdd);
        // Create a new Time object with the updated time
        return new Time(calendar.getTimeInMillis());
    }
}