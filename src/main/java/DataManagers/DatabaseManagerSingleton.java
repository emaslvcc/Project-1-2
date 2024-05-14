package DataManagers;

import java.sql.*;

public class DatabaseManagerSingleton
{
    private static DatabaseManagerSingleton instance;
    public Connection con;

    private DatabaseManagerSingleton()
    {
        String url = "jdbc:mysql://localhost:3306/quacko";
        String username = "joris";
        String password = "joris";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection Established successfully");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManagerSingleton getInstance()
    {
        if (instance == null) {
            instance = new DatabaseManagerSingleton();
        }
        return instance;
    }

    public Connection getConnection()
    {
        return con;
    }

    public static ResultSet processStatement(String statement) throws SQLException, ClassNotFoundException
    {
        Connection con = getInstance().getConnection();
        PreparedStatement stat = con.prepareStatement(statement);
        return stat.executeQuery();

    }

    public static int processUpdate(String statement) throws SQLException {
        Connection con = getInstance().getConnection();
        PreparedStatement stat = con.prepareStatement(statement);
        return stat.executeUpdate();
    }

    public static void main(String[] args) {

    }
}

