package DataManagers;

import Calculators.DistanceCalculatorHaversine;
import javafx.scene.control.TextField;
import java.util.Map;

/**
 * The GetUserData class extends DataBaseReader and provides functionality to get user input
 * for postal codes and create corresponding PostCode objects.
 */
public class GetUserData extends DataBaseReader{

    protected PostCode startPostCode, endPostCode;

    /**
     * Takes user's input and creates a PostCode object.
     * 
     * @param dataMap The map containing postal code data.
     * @return The PostCode object created based on user input.
     */
    private PostCode getZipCode(Map<String, double[]> dataMap, String zipCode) {
        return createPostCode(dataMap, zipCode);
    }

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
            saveNewPostCode(zipCode);
            return createPostCode(dataMap, zipCode);
        }
    }

    public double calculateAfterPressedButton(PostCode startPostCode, PostCode endPostCode) {
        //calculate the distance (two different methods)
        DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(startPostCode, endPostCode);

        return calc1.getDistance();
    }
    protected PostCode getStartZip(TextField startCodeField) throws Exception {
        String startCode = startCodeField.getText().toUpperCase();
        validatePostcode(startCode);
        startPostCode = getZipCode(dataMap, startCode );
        return startPostCode;
    }
    protected PostCode getEndZip(TextField endCodeField) throws Exception{

        String endCode = endCodeField.getText().toUpperCase();
        validatePostcode(endCode);
        endPostCode = getZipCode(dataMap, endCode);
        return endPostCode;
    }

    private void validatePostcode(String postcode) throws Exception {
        if (postcode.length() != 6) {
            throw new Exception("Postcode " + postcode + " is invalid: incorrect length.");
        } else if (Character.isDigit(postcode.charAt(4)) || Character.isDigit(postcode.charAt(5))) {
            throw new Exception("Postcode " + postcode + " is invalid: incorrect format.");
        }

        for (int i = 0; i < 4; i++) {
            if(!Character.isDigit(postcode.charAt(i))) {
                throw new Exception("Postcode " + postcode + " is invalid: incorrect format.");
            }
        }
    }
}