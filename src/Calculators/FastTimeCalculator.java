package Calculators;

public class FastTimeCalculator extends GenericCalculator{

    private final double fastWalkSpeed = 7.283038;
    private static final double fastCyclingSpeed = 24.928449;

    public FastTimeCalculator(Double distance) {
        this.walkingTime = calculateTime(distance, fastWalkSpeed);
        this.cyclingTime = calculateTime(distance, fastCyclingSpeed);
    }
}
