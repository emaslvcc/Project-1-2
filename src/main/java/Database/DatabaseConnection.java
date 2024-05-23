package Database;

import java.sql.*;

public class DatabaseConnection {
    static String URL = "jdbc:mysql://localhost:3306/gtfs";
    static String USER = "BCS1510";
    static String PASSWORD = "BCS1510";

    public static Connection getConnection() throws SQLException {
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
