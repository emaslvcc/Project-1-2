package Calculators;

import DataManagers.PostCode;
import java.lang.Math;

public class DistanceCalculator {

    private PostCode postCode1;
    private PostCode postCode2;
    
    public DistanceCalculator(PostCode postCode1, PostCode postCode2) {
        this.postCode1 = postCode1;
        this.postCode2 = postCode2;
    }

    public static double calculateDistancePythagoras(PostCode postCode1, PostCode postCode2) {
        double longitudes = (postCode2.longitude - postCode1.longitude) * (postCode2.longitude - postCode1.longitude);
        double latitudes = (postCode2.latitude - postCode1.latitude) * (postCode2.latitude - postCode1.latitude);
        return Math.sqrt(longitudes + latitudes);
    }

    //public double distanceCalculatorHaversine(PostCode postCode1, PostCode postCode2) {

    //}

    public static void main(String[] args) {
        PostCode postCode1 = new PostCode("6211AL", 50.85523285, 5.692237193);
        PostCode postCode2 = new PostCode("6212BN", 50.83936907, 5.688164923);
        DistanceCalculator calc = new DistanceCalculator(postCode1, postCode2);
        System.out.println(calculateDistancePythagoras(postCode1, postCode2));
    }
}
