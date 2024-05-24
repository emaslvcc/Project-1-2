package Calculators;

import DataManagers.Node;
import DataManagers.PostCode;

/**
 * The DistanceCalculatorHaversine class implements the generic Calculator
 * interface to calculate the
 * aerial distance between two PostCode objects with the Haversine formula.
 */
public class DistanceCalculatorHaversine implements DistanceCalculator {

    private double distance;
    private double latitude1, latitude2, longitude1, longitude2;

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
     * 
     * @param postCode1 The first PostCode object.
     * @param postCode2 The second PostCode object.
     * @return The distance between the corresponding coordinates.
     */
    public double calculate(PostCode postCode1, PostCode postCode2) {

        // Radius of the earth in kilometers
        final int earthRadius = 6371;

        latitude1 = postCode1.getLatitude();
        latitude2 = postCode2.getLatitude();
        longitude1 = postCode1.getLongitude();
        longitude2 = postCode2.getLongitude();

        // Conversion of degrees to radians
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        // Formula application
        double chordLengthParameter = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(chordLengthParameter), Math.sqrt(1 - chordLengthParameter));
        return earthRadius * centralAngle;
    }

    public static double calculate(double longitude1, double latitude1, double longitude2, double latitude2) {

        // Radius of the earth in kilometers
        final int earthRadius = 6371;

        // Conversion of degrees to radians
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        // Formula application
        double chordLengthParameter = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(chordLengthParameter), Math.sqrt(1 - chordLengthParameter));
        return earthRadius * centralAngle;
    }

    public static double haversineDistance(Node node1, Node node2) {
        double R = 6371.0; // Earth's radius in kilometers
        double lat1 = Math.toRadians(node1.getLat());
        double lon1 = Math.toRadians(node1.getLon());
        double lat2 = Math.toRadians(node2.getLat());
        double lon2 = Math.toRadians(node2.getLon());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // returns the distance in kilometers
    }

    /**
     * Retrieves the calculated distance.
     * 
     * @return The aerial distance between postal code addresses in kilometres.
     */
    public double getDistance() {
        return this.distance;
    }
}