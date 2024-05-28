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

    /**
     * Calculates the distance between the start and end postal codes using the Haversine formula.
     *
     * @param startPostCode The starting postal code.
     * @param endPostCode   The ending postal code.
     * @return The distance between the start and end postal codes.
     */
    public double calculateAfterPressedButton(PostCode startPostCode, PostCode endPostCode) {
        DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(startPostCode, endPostCode);
        return calc1.getDistance();
    }

    /**
     * Gets the starting postal code from the user input.
     *
     * @param startCodeField The text field containing the starting postal code.
     * @return The PostCode object for the starting postal code.
     * @throws Exception If the postal code is invalid.
     */
    protected PostCode getStartZip(JTextField startCodeField) throws Exception {
        String startCode = startCodeField.getText().toUpperCase();
        String returnValue = validatePostcode(startCode);
        if(!returnValue.isEmpty()){      // if this returns a string then there was an error
            JOptionPane.showMessageDialog(null, returnValue);
            throw new Exception(returnValue);
        }
        startPostCode = createPostCode(dataBaseReader.dataMap, startCode );
        return startPostCode;
    }

    /**
     * Gets the ending postal code from the user input.
     *
     * @param endCodeField The text field containing the ending postal code.
     * @return The PostCode object for the ending postal code.
     * @throws Exception If the postal code is invalid.
     */
    protected PostCode getEndZip(JTextField endCodeField) throws Exception{
        String endCode = endCodeField.getText().toUpperCase();
        String returnValue = validatePostcode(endCode);
        if(!returnValue.isEmpty()){      // if this returns a string then there was an error
            JOptionPane.showMessageDialog(null, returnValue);
            throw new Exception(returnValue);
        }
        endPostCode = createPostCode(dataBaseReader.dataMap, endCode);
        return endPostCode;
    }

    /**
     * Validates the format and correctness of a postal code.
     *
     * @param postcode The postal code to validate.
     * @return An error message if the postal code is invalid, otherwise an empty string.
     */
    public String validatePostcode(String postcode){
        if(postcode == null){
            return "Postcode is null";
        }
        if (postcode.length() != 6) {
            return "Postcode " + postcode + " is invalid: incorrect length.";
        } else if (Character.isDigit(postcode.charAt(4)) || Character.isDigit(postcode.charAt(5))) {
            return "Postcode " + postcode + " is invalid: incorrect format.";
        } else if (postcode.charAt(0) != '6' || postcode.charAt(1) != '2' ||  (postcode.charAt(2) != '1' && postcode.charAt(2) != '2') || postcode.charAt(3) == '0'){
            return "Postcode " + postcode + " is invalid: not in Maastricht.";
        }

        for (int i = 0; i < 4; i++) {
            if(!Character.isDigit(postcode.charAt(i))) {
                return "Postcode " + postcode + " is invalid: incorrect format.";
            }
        }
        return "";
    }
}