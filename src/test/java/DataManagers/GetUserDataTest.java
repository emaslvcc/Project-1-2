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
        assert(!user.validatePostcode(";lkjaskldjf").isEmpty());
        assert(!user.validatePostcode("GW6224").isEmpty());
        assert(!user.validatePostcode("2").isEmpty());
        assert(!user.validatePostcode("622GGW").isEmpty());
        assert(!user.validatePostcode("_224GW").isEmpty());
        assert(!user.validatePostcode("6882ER").isEmpty());
        assert(!user.validatePostcode(null).isEmpty());
        assert(user.validatePostcode("6224GW").isEmpty());
    }

}