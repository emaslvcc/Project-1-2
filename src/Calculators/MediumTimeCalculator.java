package Calculators;

/**
 * The MediumTimeCalculator class extends the GenericCalculator class and calculates walking and 
 * cycling times based on the provided distance using predefined medium walking and cycling speeds.
 */
public class MediumTimeCalculator extends GenericCalculator {

    private static final double mediumCyclingSpeed = 19.938204;
    private final double mediumWalkSpeed = 5.0148;

    /**
     * Constructs a MediumTimeCalculator object and calculates walking and cycling times
     * based on the provided distance.
     * 
     * @param distance The distance for which to calculate walking and cycling times.
     */
    public MediumTimeCalculator(Double distance) {
        this.walkingTime = calculateTime(distance, mediumWalkSpeed);
        this.cyclingTime = calculateTime(distance, mediumCyclingSpeed);
    }
}