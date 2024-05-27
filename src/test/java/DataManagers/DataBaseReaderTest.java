package DataManagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseReaderTest {

    String str ="{  \"latitude\": \"50.851539636666665\",  \"longitude\": \"5.718201086666666\",  \"postcode\": \"6224GW\"}";
    String str2 ="{  \"latitude\": \"0\",  \"longitude\": \"-92.9\",  \"postcode\": \"6224GW\"}";
    String str3 ="{  \"latitude\": \"-13.0\",  \"longitude\": \"0\",  \"postcode\": \"6224GW\"}";
    @Test
    @DisplayName("Test api response latitude extraction")
    void extractLatitude(){
        DataBaseReader data = new DataBaseReader();
        assertEquals(50.851539636666665, data.extractLatitude(str));
        assertEquals(0, data.extractLatitude(str2));
        assertEquals(-13.0, data.extractLatitude(str3));
    }

    @Test
    @DisplayName("Test api response longitude extraction")
    void extractLongitude(){
        DataBaseReader data = new DataBaseReader();
        assertEquals(5.718201086666666, data.extractLongitude(str));
        assertEquals(-92.9, data.extractLongitude(str2));
        assertEquals(0, data.extractLongitude(str3));
    }

    @Test
    void updateCSVFile() {

    }
}