package Calculators;

public class DistanceCalculator {

    // The Haversine formula
    public static double distanceCalculator(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the earth in kilometers.
        final int R = 6371;

        // Convert degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Apply formula
        double chordLengthParameter = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(chordLengthParameter), Math.sqrt(1 - chordLengthParameter));

        // Calculate distance
        double distance = R * centralAngle;

        return distance;
    }

}
