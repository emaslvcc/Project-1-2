package Calculators;

import DataManagers.PostCode;

public class DistanceCalculatorHaversine implements Calculator {

    private double distance;

    public DistanceCalculatorHaversine(PostCode postCode1, PostCode postCode2) {
        this.distance = calculate(postCode1, postCode2);
    }

    @Override
    /**
     * This method calculates the aerial distance between postal code addresses
     * @param postCode1 post code address 1
     * @param postCode2 post code address 2
     * @return the distance between the postal code addresses
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

        // Apply formula
        double chordLengthParameter = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                                    + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(chordLengthParameter), Math.sqrt(1 - chordLengthParameter));
        double distance = earthRadius * centralAngle;

        return distance;
    }

    public double getDistance() {
        return this.distance;
    }
}