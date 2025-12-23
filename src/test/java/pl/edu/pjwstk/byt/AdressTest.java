package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdressTest {

    private Adress adress;

    @BeforeEach
    void setUp() {
        adress = new Adress("123 Main St", "Warsaw", "Poland", "00-001");
    }

    @Test
    void constructorShouldSetFieldsCorrectly() {
        assertEquals("123 Main St", adress.getStreet());
        assertEquals("Warsaw", adress.getCity());
        assertEquals("Poland", adress.getCountry());
        assertEquals("00-001", adress.getZipCode());
    }

    @Test
    void updateAdressShouldChangeFields() {
        adress.UpdateAdress("456 Elm St", "Krakow", "Poland", "31-000");

        assertEquals("456 Elm St", adress.getStreet());
        assertEquals("Krakow", adress.getCity());
        assertEquals("Poland", adress.getCountry());
        assertEquals("31-000", adress.getZipCode());
    }

    @Test
    void formatForShippingShouldReturnFormattedString() {
        String expected = "123 Main St, 00-001 Warsaw, Poland";
        assertEquals(expected, adress.formatForShipping());

        adress.UpdateAdress("456 Elm St", "Krakow", "Poland", "31-000");
        String expectedUpdated = "456 Elm St, 31-000 Krakow, Poland";
        assertEquals(expectedUpdated, adress.formatForShipping());
    }
}
