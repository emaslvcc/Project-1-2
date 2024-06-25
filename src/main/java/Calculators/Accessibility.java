package Calculators;

import DataManagers.PostCode;
import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Accessibility {
    // ExecutorService for managing threads
    private ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust the thread pool size as needed

    /**
     * Updates accessibility scores for all zip codes in the specified table and category.
     *
     * @param conn      Database connection
     * @param field     Field name for coordinates
     * @param tableName Table name to update
     * @param category  Category of the destinations
     */
    private void updateAccessibilityTable(Connection conn, String field, String tableName, String category) {
        List<String> zipCodes = getZipCodes(conn);
        List<Future<Void>> futures = new ArrayList<>();

        // Submit tasks for each zip code
        for (String zipcode : zipCodes) {
            futures.add(updateAccessibilityTableOnce(conn, zipcode, field, tableName, category));
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates accessibility score for a single zip code using multithreading.
     *
     * @param conn      Database connection
     * @param zipcode   Zip code to update
     * @param field     Field name for coordinates
     * @param tableName Table name to update
     * @param category  Category of the destinations
     * @return Future representing the task
     */
    public Future<Void> updateAccessibilityTableOnce(Connection conn, String zipcode, String field, String tableName, String category) {
        Callable<Void> task = () -> {
            int finalScore = getCount(conn, zipcode, category, field);
            String updateQuery = "UPDATE " + tableName + " SET " + category + " = ? WHERE zipcode = ?";

            try (PreparedStatement stat = conn.prepareStatement(updateQuery)) {
                stat.setInt(1, finalScore);
                stat.setString(2, zipcode);
                stat.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update score in " + tableName, e);
            }
            return null;
        };

        return executor.submit(task);
    }

    /**
     * Retrieves all zip codes from the post_codes table.
     *
     * @param conn Database connection
     * @return List of zip codes
     */
    private List<String> getZipCodes(Connection conn) {
        List<String> zipCodes = new ArrayList<>();
        String query = "SELECT zipcode FROM post_codes;";

        try (PreparedStatement stat = conn.prepareStatement(query);
             ResultSet rs = stat.executeQuery()) {

            while (rs.next()) {
                zipCodes.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return zipCodes;
    }

    /**
     * Retrieves coordinates for a public transport destination based on stop_id.
     *
     * @param conn        Database connection
     * @param destination Destination ID
     * @param field       Field name for coordinates
     * @param category    Category of the destination
     * @return Coordinates of the destination
     */
    private double[] getCordsForDestPT(Connection conn, String destination, String field, String category) {
        double[] cords = new double[2];

        String query = "SELECT stop_lat, stop_lon FROM stops WHERE stop_id = ?;";
        try (PreparedStatement stat = conn.prepareStatement(query)) {
            stat.setString(1, destination);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    cords[0] = rs.getDouble("stop_lat");
                    cords[1] = rs.getDouble("stop_lon");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cords;
    }

    /**
     * Retrieves coordinates for a generic destination based on id.
     *
     * @param conn        Database connection
     * @param destination Destination ID
     * @param field       Field name for coordinates
     * @param category    Category of the destination
     * @return Coordinates of the destination
     */
    private double[] getCordsForDest(Connection conn, long destination, String field, String category) {
        double[] cords = new double[2];

        String query = "SELECT longitude, latitude FROM " + field + " WHERE id = ?;";

        try (PreparedStatement stat = conn.prepareStatement(query)) {
            stat.setLong(1, destination);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    cords[0] = rs.getDouble("latitude");
                    cords[1] = rs.getDouble("longitude");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cords;
    }

    /**
     * Retrieves all destination IDs for a specific category.
     *
     * @param conn     Database connection
     * @param category Category of the destinations
     * @return List of destination IDs
     */
    private List<Long> getDestinations(Connection conn, String category) {
        List<Long> destinationsID = new ArrayList<>();

        String query = "SELECT DISTINCT id " +
                "FROM ( " +
                "    SELECT id FROM shops WHERE category = ? " +
                "    UNION " +
                "    SELECT id FROM amenities WHERE category = ? " +
                "    UNION " +
                "    SELECT id FROM tourism WHERE category = ? " +
                ") AS combined_categories;";

        try (PreparedStatement stat = conn.prepareStatement(query)) {
            stat.setString(1, category);
            stat.setString(2, category);
            stat.setString(3, category);

            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    destinationsID.add(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return destinationsID;
    }

    /**
     * Retrieves all public transport destination IDs within a specific area.
     *
     * @param conn Database connection
     * @return List of destination IDs
     */
    private List<String> getDestinationsPT(Connection conn) {
        List<String> destinationsID = new ArrayList<>();

        String query = "SELECT stop_id FROM stops WHERE (stop_lat BETWEEN 50.803792 AND 50.9)\n" +
                "      AND (stop_lon BETWEEN 5.640811 AND 5.739475)";

        try (PreparedStatement stat = conn.prepareStatement(query)) {

            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    destinationsID.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return destinationsID;
    }

    /**
     * Retrieves coordinates for a specific zip code.
     *
     * @param conn    Database connection
     * @param zipCode Zip code
     * @return Coordinates of the zip code
     */
    public double[] getCoords(Connection conn, String zipCode) {
        double[] dataArr = new double[2];

        String query = "SELECT latitude, longitude FROM post_codes WHERE zipcode = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
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

    /**
     * Counts the number of destinations within a specified distance (0.175 km) from the given zip code.
     *
     * @param conn     Database connection
     * @param zipcode  Zip code
     * @param category Category of the destinations
     * @param field    Field name for coordinates
     * @return Count of destinations within the specified distance
     */
    private int getCount(Connection conn, String zipcode, String category, String field) {
        int count = 0;
        double[] zipcodeCords = getCoords(conn, zipcode);

        if (category.equals("Public_Transport")) {
            List<String> destinations = getDestinationsPT(conn);

            for (int i = 0; i < destinations.size(); i++) {
                double[] destCords = getCordsForDestPT(conn, destinations.get(i), field, category);
                if (DistanceCalculatorHaversine.calculate(zipcodeCords[0], zipcodeCords[1], destCords[0], destCords[1]) <= 0.175) {
                    count++;
                }
            }

        } else {
            List<Long> destinations = getDestinations(conn, category);

            for (int i = 0; i < destinations.size(); i++) {
                double[] destCords = getCordsForDest(conn, destinations.get(i), field, category);
                if (DistanceCalculatorHaversine.calculate(zipcodeCords[0], zipcodeCords[1], destCords[0], destCords[1]) <= 0.175) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Updates accessibility scores for a new zip code.
     *
     * @param zipcode Zip code to update
     */
    public void newZipCodeAcc(String zipcode) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Starting accessibility update...");

            List<Future<Void>> futures = new ArrayList<>();
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Public_Transport"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Miscellaneous"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Financial_Services"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Public_Services"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Recreation_and_Entertainment"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Transportation"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "amenities", "amenities_accessibility", "Utilities"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "tourism", "tourism_accessibility", "tourism"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "shops", "shops_accessibility", "service"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "shops", "shops_accessibility", "speciality_store"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "shops", "shops_accessibility", "personal_care"));
            futures.add(updateAccessibilityTableOnce(conn, zipcode, "shops", "shops_accessibility", "supermarket"));

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("All tables updated successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    /**
     * Returns the post codes and assigns their scores to them.
     *
     * @return List of PostCode objects containing zip code, latitude, longitude, and accessibility score
     */
    public ArrayList<PostCode> returnAccessibilityScores() {
        ArrayList<PostCode> postCodes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> zipCodes = getZipCodes(conn);

            zipCodes.parallelStream().forEach(zipCode -> {
                double[] coords = getCoords(conn, zipCode);
                PostCode postCode = new PostCode(zipCode, coords[0], coords[1]);
                double score = returnScore(conn, zipCode);
                postCode.setScore(score);
                postCodes.add(postCode);
            });

        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }

        return postCodes;
    }

    /**
     * Returns the accessibility score for a specific zip code.
     *
     * @param zipCode Zip code
     * @return Accessibility score
     */
    private double returnScore(Connection conn, String zipCode) {
        double score = 0;
        String sqlStatement = "SELECT total_weighted_score FROM gtfs.weighted_accessibility_scores WHERE zipcode = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlStatement)) {
            pstmt.setString(1, zipCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    score = rs.getDouble("total_weighted_score");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving score for zip code: " + zipCode, e);
        }

        return score;
    }


    // Main method for recalculating the whole accessibility if needed
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            long startTime = System.currentTimeMillis(); // Start timer

            Accessibility accessibility = new Accessibility();
            System.out.println("Starting accessibility update...");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Public_Transport");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Miscellaneous");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Financial_Services");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Public_Services");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Recreation_and_Entertainment");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Transportation");
            accessibility.updateAccessibilityTable(conn, "amenities", "amenities_accessibility", "Utilities");
            accessibility.updateAccessibilityTable(conn, "tourism", "tourism_accessibility", "tourism");
            accessibility.updateAccessibilityTable(conn, "shops", "shops_accessibility", "service");
            accessibility.updateAccessibilityTable(conn, "shops", "shops_accessibility", "speciality_store");
            accessibility.updateAccessibilityTable(conn, "shops", "shops_accessibility", "personal_care");
            accessibility.updateAccessibilityTable(conn, "shops", "shops_accessibility", "supermarket");

            long endTime = System.currentTimeMillis(); // End timer
            long totalTime = endTime - startTime;
            System.out.println("Total time taken: " + totalTime + " milliseconds");
            System.out.println("Accessibility update completed.");
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database connection", e);
        }
    }
}
