package Calculators;

import DataManagers.PostCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorHaversineTest {
    @Test
    void calculate() {      // get distance just calls calculate so we don't need to write another test for getDistance
        DistanceCalculatorHaversine calc = new DistanceCalculatorHaversine(new PostCode("this does not matter",65.70077619188554, -18.222601673691358), new PostCode("this does not matter",5.573510826636069, -68.51212002026169));
        assertEquals(7728, (int) calc.getDistance());          // we round since distance long enough so that we would see a difference. result taken from https://www.vcalc.com/wiki/vcalc/haversine-distance
    }
}