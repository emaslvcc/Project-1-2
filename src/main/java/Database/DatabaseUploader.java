package Database;

import java.sql.*;

public class DatabaseUploader {

    public static Connection myCon;

    public static void init() {
        try {
            myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/gtfs", "joris", "joris");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}