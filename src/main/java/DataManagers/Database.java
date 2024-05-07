package DataManagers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.sql.*;

public class Database
{
    /**
     You need to have this file: src/main/resources/Data/DatabaseCredentials.json      that looks like this fill in your username and password
     {
         "username": "",
         "password": ""
     }
     */
    public Connection databaseConnection() throws ClassNotFoundException, SQLException
    {
        String[] usernamePassword = getUsernamePassword();
        String url
                = "jdbc:mysql://localhost:3306/gtfs"; // table details
        String username = usernamePassword[0]; // MySQL credentials
        String password = usernamePassword[1];
        Class.forName(
                "com.mysql.cj.jdbc.Driver"); // Driver name
        Connection con = DriverManager.getConnection(url, username, password);
        return con;
    }


    private String[] getUsernamePassword(){
        JSONParser parser = new JSONParser();
        String[] usernamePassword = new String[2];
        try {
            Object obj = parser.parse(new FileReader("src/main/resources/Data/DatabaseCredentials.json"));
            JSONObject jsonObject = (JSONObject)obj;
            usernamePassword[0] = (String)jsonObject.get("username");
            usernamePassword[1] = (String)jsonObject.get("password");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return usernamePassword;
    }

    public static void main(String[] args) {

    }
}
