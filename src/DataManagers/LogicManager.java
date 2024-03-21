package DataManagers;

import Calculators.AverageTimeCalculator;
import Calculators.TimeCalculator;
import UserInterface.MapViewer;
import UserInterface.PaneCreators;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class LogicManager extends GetUserData {

    protected int time;
    protected double distance;

    protected void calculateLogic(TextField startCodeField, TextField endCodeField, ChoiceBox<String> modeBox){

        System.out.println("Calculating");
        createHashMap();
        try {
            startPostCode = getStartZip(startCodeField);
            Thread.sleep(6000);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            endPostCode = getEndZip(endCodeField);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        MapViewer.updateCord(startPostCode,endPostCode);
        MapViewer.drawLine();
        distance = Math.round(calculateAfterPressedButton(startPostCode,endPostCode)* 100d) / 100d;

        TimeCalculator timeCalc = new AverageTimeCalculator(distance);

        if ((modeBox.getValue()).equals("Walk")){
            time = (int) (Math.round(timeCalc.getWalkingTime()));
        }
        else if ((modeBox.getValue()).equals("Bike")){
            time = (int) (Math.round(timeCalc.getCyclingTime()));
        }

    }
}
