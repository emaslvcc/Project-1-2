package Calculators;

public class MediumTimeCalculator extends GenericCalculator {

    private static final double mediumCyclingSpeed = 19.938204;
    private final double mediumWalkSpeed = 5.0148;

    public MediumTimeCalculator(Double distance) {
        this.walkingTime = calculateTime(distance, mediumWalkSpeed);
        this.cyclingTime = calculateTime(distance, mediumCyclingSpeed);
    }
}

