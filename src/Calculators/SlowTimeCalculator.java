package Calculators;

/**
 * The SlowTimeCalculator class extends the GenericCalculator class and calculates walking and 
 * cycling times based on the provided distance using predefined slow walking and cycling speeds.
 */
public class SlowTimeCalculator extends TimeCalculator {

    private final double slowWalkSpeed = 2.02777344;
    private final double slowCyclingSpeed = 13.92383;

    /**
     * Constructs a SlowTimeCalculator object and calculates walking and cycling times
     * based on the provided distance.
     * 
     * @param distance The distance for which to calculate walking and cycling times.
     */
    public SlowTimeCalculator(Double distance){
        this.walkingTime = calculateTime(distance, slowWalkSpeed);
        this.cyclingTime = calculateTime(distance, slowCyclingSpeed);
    }
}