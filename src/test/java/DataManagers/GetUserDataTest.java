package DataManagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class GetUserDataTest {

    @Test
    @DisplayName("Test if postal code format validation works")
    void sendPostRequest() {
        GetUserData user = new GetUserData();
        assertThrows(Exception.class, () -> user.validatePostcode(";lkjaskldjf"));
        assertThrows(Exception.class, () -> user.validatePostcode("GW6224"));
        assertThrows(Exception.class, () -> user.validatePostcode("2"));
        assertThrows(Exception.class, () -> user.validatePostcode("622GGW"));
        assertThrows(Exception.class, () -> user.validatePostcode("_224GW"));
        assertThrows(Exception.class, () -> user.validatePostcode(""));
        assertThrows(Exception.class, () -> user.validatePostcode(null));
    }

}