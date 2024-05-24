package Database;

import java.sql.*;

public class DatabaseUploader {

    public static Connection myCon;

    public static void init() {
        try {
            myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/gtfs", "BCS1510", "BCS1510");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}