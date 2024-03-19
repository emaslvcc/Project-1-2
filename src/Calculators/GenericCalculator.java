package Calculators;

public abstract class GenericCalculator{

     double walkingTime;
     double cyclingTime;
     protected double calculateTime(double distance, double speed){
        double time = distance / speed;
        return time;
    };

    public double getWalkingTime() {
        return this.walkingTime;
    }
    public double getCyclingTime() {
        return this.cyclingTime;
    }

}
