package DataManagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class APICallerTest {


    @Test
    @DisplayName("Checking API connection")
    void sendPostRequest() throws InterruptedException {
        assertFalse(APICaller.sendPostRequest("6224GW").isEmpty());
    }
}