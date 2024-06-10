package DataManagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Bus.BusConnectionDev;

import static org.junit.jupiter.api.Assertions.*;

class NotripTest {

    @Test
    @DisplayName("Test when there is no direct connection")
    void testNoDirectConnection_a() {
        // "6227XB"
        double startLat = 50.839170;
        double startLon = 5.734520;

        // "6223BJ"
        double endLat = 50.877370;
        double endLon = 5.687570;

        Exception exception = assertThrows(Exception.class, () -> {
            BusConnectionDev.testClass = true;
            BusConnectionDev.busLogic(startLat, startLon, endLat, endLon);
        });

        String expectedMessage = "No direct bus connection";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Test when there is direct connection")
    void testNoDirectConnection_b() {
        // "6227XB"
        double startLat = 50.839170;
        double startLon = 5.734520;

        // "6211AL"
        double endLat = 50.85523285;
        double endLon = 5.692237193;

        Exception exception = assertThrows(Exception.class, () -> {
            BusConnectionDev.busLogic(startLat, startLon, endLat, endLon);
        });

        String expectedMessage = "No direct bus connection";
        String actualMessage = exception.getMessage();
        assertFalse(actualMessage.contains(expectedMessage));
    }

}