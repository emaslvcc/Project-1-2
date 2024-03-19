package UserInterface;

import Calculators.*;
import DataManagers.GetUserData;
import DataManagers.PostCode;

import java.util.Scanner;

public class Launcher extends GetUserData {
    private double walkingTime;
    private double cyclingTime;
    private GenericCalculator timeCalc;
    public static void main(String[] args) {

        //call a method to start a program
        Launcher main = new Launcher();
        main.callApp();
    }

    private void callApp() {

        //TODO scanners for debugging - later replaced with action listener
        Scanner scanner = new Scanner(System.in);
        Scanner scanner1 = new Scanner(System.in);

        createHashMap();

        //create objects of both start and end zipCodes
        PostCode startPostCode = getZipCode(dataMap, "start", scanner );
        PostCode endPostCode = getZipCode(dataMap, "end", scanner1);

        //calculate the distance (two different methods)
        DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(startPostCode, endPostCode);
        DistanceCalculatorPythagoras calc2 = new DistanceCalculatorPythagoras(startPostCode, endPostCode);

        Scanner scanner2 = new Scanner(System.in);
        System.out.println("What speed do you want: slow (1), medium (2), fast (3)?");
        int choice = scanner.nextInt();

        if (choice == 1){
            timeCalc = new SlowTimeCalculator(calc1.getDistance());
            walkingTime = timeCalc.getWalkingTime();
            cyclingTime = timeCalc.getCyclingTime();
        }
        else if (choice == 2){
            timeCalc = new MediumTimeCalculator(calc1.getDistance());
            walkingTime = timeCalc.getWalkingTime();
            cyclingTime = timeCalc.getCyclingTime();
        }else {
            timeCalc = new FastTimeCalculator(calc1.getDistance());
            walkingTime = timeCalc.getWalkingTime();
            cyclingTime = timeCalc.getCyclingTime();
        }
        //debugging
        System.out.println("The distance with the Haversine formula is: " + String.format("%.3f", calc1.getDistance()) + " kilometers.");
        System.out.println("Approximate walking time: " + walkingTime + " minutes, cycling time: "+ cyclingTime +" minutes");
        System.out.println("The distance with the Pythagorean formula is: " + String.format("%.3f", calc2.getDistance()) + " kilometers.");
        System.out.println("Approximate walking time: " + walkingTime + " minutes, cycling time: "+ cyclingTime + " minutes.");
    }

}
