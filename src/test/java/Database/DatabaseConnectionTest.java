package Database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    @DisplayName("Test Database connection")
    void getConnection() throws SQLException {
        assert(DatabaseConnection.getConnection() != null);
    }
}