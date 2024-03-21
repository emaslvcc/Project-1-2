package DataManagers;

import Calculators.DistanceCalculatorHaversine;

import java.util.Map;

/**
 * The GetUserData class extends DataBaseReader and provides functionality to get user input
 * for postal codes and create corresponding PostCode objects.
 */
public class GetUserData extends DataBaseReader{

    /**
     * Takes user's input and creates a PostCode object.
     * 
     * @param dataMap The map containing postal code data.
     * @return The PostCode object created based on user input.
     */
    protected PostCode getZipCode(Map<String, double[]> dataMap, String zipCode) {
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
}