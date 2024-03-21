package Calculators;

import java.lang.Math;
import DataManagers.PostCode;

/**
 * The DistanceCalculatorPythagoras class implements the generic Calculator interface to calculate the
 * aerial distance between two PostCode objects with the Pythagorean formula.
 */
public class DistanceCalculatorPythagoras implements DistanceCalculator {

    public double distance;

    /**
     * Constructs a DistanceCalculatorPythagoras object and calculates the distance
     * between two PostCode objects using Pythagoras formula.
     * 
     * @param postCode1 The first PostCode object.
     * @param postCode2 The second PostCode object.
     */
    public DistanceCalculatorPythagoras(PostCode postCode1, PostCode postCode2) {
        this.distance = calculate(postCode1, postCode2);
    }
    
    @Override
    /**
     * This method calculates the aerial distance between postal code addresses.
     * @param postCode1 The first PostCode object.
     * @param postCode2 The second PostCode object.
     * @return The distance between the corresponding coordinates.
     */
    public double calculate(PostCode postCode1, PostCode postCode2) {

        // Radius of the earth in kilometers
        final double earthRadius = 6371.0;

        // Conversion to radians
        double latitude1 = Math.toRadians(postCode1.getLatitude());
        double latitude2 = Math.toRadians(postCode2.getLatitude());
        double longitude1 = Math.toRadians(postCode1.getLongitude());
        double longitude2 = Math.toRadians(postCode2.getLongitude());

        // Formula application
        double latDistance = latitude2 - latitude1;
        double lonDistance = longitude2 - longitude1;
        return Math.sqrt(latDistance * latDistance + lonDistance * lonDistance) * earthRadius;
    }

    /**
     * Retrieves the calculated distance.
     * @return The aerial distance between postal code addresses in kilometres.
     */
    public double getDistance() {
        return this.distance;
    }
}