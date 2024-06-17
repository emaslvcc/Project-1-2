package Bus;

import Calculators.AverageTimeCalculator;
import Calculators.DistanceCalculatorHaversine;
import Calculators.TimeCalculator;

public class testtime {
    public static void main(String[] args) {
        double distanceBetweenTwoZipCodes = DistanceCalculatorHaversine.calculate(5.734281545187904, 50.838624794887046,
                5.7368522776059345, 50.84267774699003);
        TimeCalculator timeCalc = new AverageTimeCalculator(distanceBetweenTwoZipCodes);
        int time = (int) (Math.round(timeCalc.getWalkingTime()));
        System.out.println(time);
    }
}
