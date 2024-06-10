package DataManagers;

import Calculators.DistanceCalculatorHaversine;
import Database.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
     * @param zipCode The zip code to create a PostCode object for.
     * @return The PostCode object created based on the provided zip code.
     */
    private PostCode createPostCode(String zipCode) {
        if (isInDatabase(zipCode)) {
            double[] dataArr = getCoorinates(zipCode);
            return new PostCode(zipCode, dataArr[0], dataArr[1]);
        } else {
            dataBaseReader.saveNewPostCode(zipCode);
            return createPostCode(zipCode);
        }
    }

    /**
     * Checks if the zip code is already in the database.
     *
     * @param zipCode The zip code to check.
     * @return True if the zip code is in the database, false otherwise.
     */
    private boolean isInDatabase(String zipCode) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "SELECT * FROM post_codes WHERE zipcode = ?")) {

            preparedStatement.setString(1, zipCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking if zip code is in the database", e);
        }
    }

    /**
     * Retrieves the coordinates for a given zip code from the database.
     *
     * @param zipCode The zip code to retrieve coordinates for.
     * @return A double array containing latitude and longitude.
     */
    private double[] getCoorinates(String zipCode) {
        double[] dataArr = new double[2];

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "SELECT latitude, longitude FROM post_codes WHERE zipcode = ?")) {

            preparedStatement.setString(1, zipCode);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    dataArr[0] = rs.getDouble("latitude");
                    dataArr[1] = rs.getDouble("longitude");
                } else {
                    throw new SQLException("No coordinates found for zip code: " + zipCode);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving coordinates for zip code: " + zipCode, e);
        }

        return dataArr;
    }

    protected PostCode getStartZip(JTextField startCodeField) throws Exception {
        String startCode = startCodeField.getText().toUpperCase();
        String returnValue = validatePostcode(startCode);
        if (!returnValue.isEmpty()) {      // if this returns a string then there was an error
            JOptionPane.showMessageDialog(null, returnValue);
            throw new Exception(returnValue);
        }
        startPostCode = createPostCode(startCode);
        return startPostCode;
    }

    protected PostCode getEndZip(JTextField endCodeField) throws Exception {
        String endCode = endCodeField.getText().toUpperCase();
        String returnValue = validatePostcode(endCode);
        if (!returnValue.isEmpty()) {      // if this returns a string then there was an error
            JOptionPane.showMessageDialog(null, returnValue);
            throw new Exception(returnValue);
        }
        endPostCode = createPostCode(endCode);
        return endPostCode;
    }

    public String validatePostcode(String postcode) {
        if (postcode == null) {
            return "Postcode is null";
        }
        if (postcode.length() != 6) {
            return "Postcode " + postcode + " is invalid: incorrect length.";
        } else if (Character.isDigit(postcode.charAt(4)) || Character.isDigit(postcode.charAt(5))) {
            return "Postcode " + postcode + " is invalid: incorrect format.";
        } else if (postcode.charAt(0) != '6' || postcode.charAt(1) != '2' || (postcode.charAt(2) != '1' && postcode.charAt(2) != '2') || postcode.charAt(3) == '0') {
            return "Postcode " + postcode + " is invalid: not in Maastricht.";
        }

        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(postcode.charAt(i))) {
                return "Postcode " + postcode + " is invalid: incorrect format.";
            }
        }
        return "";
    }
}
