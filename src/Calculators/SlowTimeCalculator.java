package Calculators;

public class SlowTimeCalculator extends GenericCalculator {

    private final double slowWalkSpeed = 2.02777344;
    private final double slowCyclingSpeed = 13.92383;

    public SlowTimeCalculator(Double distance){
        this.walkingTime = calculateTime(distance, slowWalkSpeed);
        this.cyclingTime = calculateTime(distance, slowCyclingSpeed);
    }

}
