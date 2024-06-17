package Calculators;

/**
 * The SlowTimeCalculator class extends the GenericCalculator class and
 * calculates walking and
 * cycling times based on the provided distance using predefined slow walking
 * and cycling speeds.
 */
public class AverageTimeCalculator extends TimeCalculator {

    private final double walkSpeed = 0.0765;
    private final double cyclingSpeed = 0.2783;

    /**
     * Constructs a SlowTimeCalculator object and calculates walking and cycling
     * times
     * based on the provided distance.
     * 
     * @param distance The distance for which to calculate walking and cycling
     *                 times.
     */
    public AverageTimeCalculator(double distance) {
        this.walkingTime = calculateTime(distance, walkSpeed);
        this.cyclingTime = calculateTime(distance, cyclingSpeed);
    }

}