package Calculators;

/**
 * The GenericCalculator class provides a base for calculating walking and cycling times.
 */
public abstract class GenericCalculator {

    double walkingTime;
    double cyclingTime;

    /**
     * Calculates the time required to cover a certain distance at a given speed.
     * 
     * @param distance The distance to cover.
     * @param speed The speed at which to cover the distance.
     * @return The time required to cover the distance at the given speed.
     */
    protected double calculateTime(double distance, double speed) {
        double time = distance / speed;
        return time;
    };

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
}