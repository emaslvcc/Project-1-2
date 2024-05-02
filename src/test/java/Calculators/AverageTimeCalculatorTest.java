package Calculators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AverageTimeCalculatorTest {

    @Test
    void calculateTime() {
        AverageTimeCalculator calc = new AverageTimeCalculator(10);
        assertEquals(10/0.5, calc.calculateTime(10,0.5));

        assertEquals(290/0.32, calc.calculateTime(290,0.32));

        assertEquals(584.45/0.3, calc.calculateTime(-584.45,0.3));
    }

    @Test
    void getWalkingTime() {
        AverageTimeCalculator calc = new AverageTimeCalculator(10);
        assertEquals(calc.walkingTime, calc.getWalkingTime());
    }

    @Test
    void getCyclingTime() {
        AverageTimeCalculator calc = new AverageTimeCalculator(10);
        assertEquals(calc.cyclingTime, calc.getCyclingTime());
    }
}