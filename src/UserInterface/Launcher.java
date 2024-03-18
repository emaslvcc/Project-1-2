package UserInterface;

import Calculators.DistanceCalculatorHaversine;
import Calculators.DistanceCalculatorPythagoras;
import Calculators.TimeCalculator;
import DataManagers.GetUserData;
import DataManagers.PostCode;

import java.util.Scanner;

public class Launcher extends GetUserData {

    public static void main(String[] args) {

        //call a method to start a program
        Launcher main = new Launcher();
        main.callApp();
    }

    private void callApp() {

        //TODO scanners for debugging - later replaced with action listener
        Scanner scanner = new Scanner(System.in);
        Scanner scanner1 = new Scanner(System.in);

        //create objects of both start and end zipCodes
        PostCode startPostCode = getZipCode(dataMap, "start", scanner );
        PostCode endPostCode = getZipCode(dataMap, "end", scanner1);

        //calculate the distance (two different methods)
        DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(startPostCode, endPostCode);
        DistanceCalculatorPythagoras calc2 = new DistanceCalculatorPythagoras(startPostCode, endPostCode);
        TimeCalculator timeCalc1 = new TimeCalculator(calc1.getDistance());
        TimeCalculator timeCalc2 = new TimeCalculator(calc1.getDistance());

        //debugging
        System.out.println("The distance with the Haversine formula is: " + String.format("%.3f", calc1.getDistance()) + " kilometers.");
        System.out.println("Approximate walking time: " + timeCalc1.getWalkingTime() + " minutes, cycling time: "+ timeCalc1.getCyclingTime() +" minutes");
        System.out.println("The distance with the Pythagorean formula is: " + String.format("%.3f", calc2.getDistance()) + " kilometers.");
        System.out.println("Approximate walking time: " + timeCalc2.getWalkingTime() + " minutes, cycling time: "+ timeCalc2.getCyclingTime() + " minutes.");
    }

}
