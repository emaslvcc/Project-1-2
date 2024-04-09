package Calculators;

import DataManagers.PostCode;

/**
 * The Calculator interface defines a base for classes that perform calculations based on two PostCode objects.
 */
public interface DistanceCalculator {
    
    /**
     * Calculates a result based on two PostCode objects.
     * 
     * @param postCode1 The first PostCode object.
     * @param postCode2 The second PostCode object.
     * @return The result of the calculation.
     */
    double calculate(PostCode postCode1, PostCode postCode2);
}