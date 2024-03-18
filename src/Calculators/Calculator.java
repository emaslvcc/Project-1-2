package Calculators;

import DataManagers.PostCode;

public interface Calculator {
    
    double calculate(PostCode postCode1, PostCode postCode2);
}
