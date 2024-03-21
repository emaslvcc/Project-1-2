package Calculators;

/**
 * The FastTimeCalculator class extends the GenericCalculator class and calculates walking and 
 * cycling times based on the provided distance using predefined fast walking and cycling speeds.
 */
public class FastTimeCalculator extends TimeCalculator {

    private final double fastWalkSpeed = 7.283038;
    private static final double fastCyclingSpeed = 24.928449;

    /**
     * Constructs a FastTimeCalculator object and calculates walking and cycling times
     * based on the provided distance.
     * 
     * @param distance The distance for which to calculate walking and cycling times.
     */
    public FastTimeCalculator(Double distance) {
        this.walkingTime = calculateTime(distance, fastWalkSpeed);
        this.cyclingTime = calculateTime(distance, fastCyclingSpeed);
    }
}