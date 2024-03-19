package DataManagers;

import java.util.Map;
import java.util.Scanner;

/**
 * The GetUserData class extends DataBaseReader and provides functionality to get user input
 * for postal codes and create corresponding PostCode objects.
 */
public class GetUserData extends DataBaseReader{

    /**
     * Takes user's input and creates a PostCode object.
     * 
     * @param dataMap The map containing postal code data.
     * @param txt Additional text to display for user input prompt.
     * @param scanner The scanner object for user input.
     * @return The PostCode object created based on user input.
     */
    protected PostCode getZipCode(Map<String, double[]> dataMap, String txt, Scanner scanner) {
        System.out.println("Enter your "+ txt +" zip code: ");
        String zipCode = scanner.nextLine();
        zipCode = zipCode.toUpperCase();

        PostCode postCode = createPostCode(dataMap, zipCode);
        return postCode;
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
            PostCode postCode = new PostCode(zipCode, dataMap.get(zipCode)[0], dataMap.get(zipCode)[1]);
            return postCode;
        } else {
            saveNewPostCode(zipCode);
            return createPostCode(dataMap, zipCode);
        }
    }
}