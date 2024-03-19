package Calculators;

import DataManagers.PostCode;

/**
 * The DistanceCalculatorHaversine class implements the generic Calculator interface to calculate the
 * aerial distance between two PostCode objects with the Haversine formula.
 */
public class DistanceCalculatorHaversine implements Calculator {

    private double distance;

    /**
     * Constructs a DistanceCalculatorHaversine object and calculates the distance
     * between two PostCode objects using the Haversine formula.
     * 
     * @param postCode1 The first PostCode object.
     * @param postCode2 The second PostCode object.
     */
    public DistanceCalculatorHaversine(PostCode postCode1, PostCode postCode2) {
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
        final int earthRadius = 6371;

        double latitude1 = postCode1.getLatitude();
        double latitude2 = postCode2.getLatitude();
        double longitude1 = postCode1.getLongitude();
        double longitude2 = postCode2.getLongitude();

        // Conversion of degrees to radians
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        // Formula application
        double chordLengthParameter = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                                    + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(chordLengthParameter), Math.sqrt(1 - chordLengthParameter));
        double distance = earthRadius * centralAngle;

        return distance;
    }

    /**
     * Retrieves the calculated distance.
     * @return The aerial distance between postal code addresses in kilometres.
     */
    public double getDistance() {
        return this.distance;
    }
}