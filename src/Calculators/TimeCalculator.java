package Calculators;

import java.sql.Time;

public class TimeCalculator {

    private double averageWalkingSpeed = 0.0833; //(5km/h in km/min) or integer we will see
    private double averageCyclingSpeed = 0.25; //(15km/h in km/min) or integer we will see
    private double walkingTime;
    private double cyclingTime;

    public TimeCalculator(Double distance){
        this.walkingTime = calculateWalkingTime(distance);
        this.cyclingTime = calculateCyclingTime(distance);
    }

    private double calculateWalkingTime(Double distance){

        double time = (distance/averageWalkingSpeed);
        return time;
    }

    private double calculateCyclingTime(Double distance){

        double time = (distance/averageCyclingSpeed);
        return time;
    }

    public double getWalkingTime() {
        return this.walkingTime;
    }
    public double getCyclingTime() {
        return this.cyclingTime;
    }
}
