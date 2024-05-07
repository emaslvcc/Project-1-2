package DataManagers;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void databaseConnection() throws SQLException, ClassNotFoundException {
        Database data = new Database();
        assertNotNull(data.databaseConnection());
    }
}