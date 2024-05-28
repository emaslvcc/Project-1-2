package Database;

import java.sql.*;

/**
 * The DatabaseConnection class provides a method to establish a connection
 * to a MySQL database.
 */
public class DatabaseConnection {
    static String URL = "jdbc:mysql://localhost:3306/gtfs";
    static String USER = "BCS1510";
    static String PASSWORD = "BCS1510";

    /**
     * Establishes and returns a connection to the database.
     *
     * @return a Connection object to the MySQL database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
