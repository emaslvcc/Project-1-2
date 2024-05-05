package DataManagers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class APICallerTest {

    @Test
    void sendPostRequest() {
        assertAll(APICaller.sendPostRequest("6224GW"));
        assertTrue(APICaller.sendPostRequest("1096AC").isEmpty());                   // response should be empty since this is a postal code from amsterdam

        // api seems to be down or our code might actually be at fault. Really don't know :(.   TODO do this :).
    }
}