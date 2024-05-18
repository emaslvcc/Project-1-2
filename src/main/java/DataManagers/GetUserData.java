package DataManagers;

import Calculators.DistanceCalculatorHaversine;

import javax.swing.*;
import java.util.Map;

/**
 * The GetUserData class extends DataBaseReader and provides functionality to get user input
 * for postal codes and create corresponding PostCode objects.
 */
public class GetUserData {

    DataBaseReader dataBaseReader = new DataBaseReader();


    protected PostCode startPostCode, endPostCode;
    /**
     * Checks if the called zip code is in the hashMap, if not calls an API, then recursively checks again.
     * 
     * @param dataMap The map containing postal code data.
     * @param zipCode The zip code to create a PostCode object for.
     * @return The PostCode object created based on the provided zip code.
     */
    private PostCode createPostCode(Map<String, double[]> dataMap, String zipCode){
        if (dataMap.containsKey(zipCode)) {
            return new PostCode(zipCode, dataMap.get(zipCode)[0], dataMap.get(zipCode)[1]);
        } else {
            dataBaseReader.saveNewPostCode(zipCode);
            return createPostCode(dataMap, zipCode);
        }
    }

    public double calculateAfterPressedButton(PostCode startPostCode, PostCode endPostCode) {
        //calculate the distance (two different methods)
        DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(startPostCode, endPostCode);

        return calc1.getDistance();
    }
    protected PostCode getStartZip(JTextField startCodeField) throws Exception {
        String startCode = startCodeField.getText().toUpperCase();
        validatePostcode(startCode);
        startPostCode = createPostCode(dataBaseReader.dataMap, startCode );
        return startPostCode;
    }
    protected PostCode getEndZip(JTextField endCodeField) throws Exception{

        String endCode = endCodeField.getText().toUpperCase();
        validatePostcode(endCode);
        endPostCode = createPostCode(dataBaseReader.dataMap, endCode);
        return endPostCode;
    }

    public void validatePostcode(String postcode) throws Exception {
        if (postcode.length() != 6) {
            JOptionPane.showMessageDialog(null, "Postcode " + postcode + " is invalid: incorrect length.");
            throw new Exception("Postcode " + postcode + " is invalid: incorrect length.");
        } else if (Character.isDigit(postcode.charAt(4)) || Character.isDigit(postcode.charAt(5))) {
            JOptionPane.showMessageDialog(null, "Postcode " + postcode + " is invalid: incorrect format.");
            throw new Exception("Postcode " + postcode + " is invalid: incorrect format.");
        }
        else if (postcode.charAt(0) != '6' || postcode.charAt(1) != '2' ||  (postcode.charAt(2) != '1' && postcode.charAt(2) != '2') || postcode.charAt(3) == '0'){
            JOptionPane.showMessageDialog(null, "Postcode " + postcode + " is invalid: not in Maastricht.");
            throw new Exception("Postcode " + postcode + " is invalid: not in Maastricht.");
        }

        for (int i = 0; i < 4; i++) {
            if(!Character.isDigit(postcode.charAt(i))) {
                JOptionPane.showMessageDialog(null, "Postcode " + postcode + " is invalid: incorrect format.");
                throw new Exception("Postcode " + postcode + " is invalid: incorrect format.");
            }
        }
    }
}