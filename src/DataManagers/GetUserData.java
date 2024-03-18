package DataManagers;

import java.util.Map;
import java.util.Scanner;

public class GetUserData extends DataBaseReader{

    //takes user's input and creates a PostCode object
    protected PostCode getZipCode(Map<String, double[]> dataMap, String txt, Scanner scanner) {

        System.out.println("Enter your "+ txt +" zip code: "); // Corrected the message
        String zipCode = scanner.nextLine();
        zipCode = zipCode.toUpperCase();

        PostCode postCode = createPostCode(dataMap, zipCode);

        return postCode;
    }

    //checks if called zip code is in the hashMap, if not calls an API, then recursively checks again
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
