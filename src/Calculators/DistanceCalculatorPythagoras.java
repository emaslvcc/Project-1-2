package Calculators;

import java.lang.Math;
import DataManagers.PostCode;

public class DistanceCalculatorPythagoras implements Calculator {

    public double distance;

    public DistanceCalculatorPythagoras(PostCode postCode1, PostCode postCode2) {
        this.distance = calculate(postCode1, postCode2);
    }
    
    @Override
    public double calculate(PostCode postCode1, PostCode postCode2) {

        // Radius of the earth in kilometers
        final double earthRadius = 6371.0;

        double latitude1 = Math.toRadians(postCode1.getLatitude());
        double latitude2 = Math.toRadians(postCode2.getLatitude());
        double longitude1 = Math.toRadians(postCode1.getLongitude());
        double longitude2 = Math.toRadians(postCode2.getLongitude());

        // Conversion of degrees to radians
        double latDistance = latitude2 - latitude1;
        double lonDistance = (longitude2 - longitude1); // * Math.cos((latitude1 + latitude2) / 2);

        double distance = Math.sqrt(latDistance * latDistance + lonDistance * lonDistance);
        return distance;
    }

    public double getDistance() {
        return this.distance;
    }
}
