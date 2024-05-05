package DataManagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostCodeTest {
    double lat =2.092834023984;
    double lon =19.29810249890238;
    PostCode test1 = new PostCode("6228RW", lat, lon);
    @Test
    @DisplayName("Test Latitude")
    void getLatitude() {
        assertEquals(lat,test1.getLatitude());
    }

    @Test
    @DisplayName("Test Longitude")
    void getLongitude() {
        assertEquals(lon,test1.getLongitude());
    }
}