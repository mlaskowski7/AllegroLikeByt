package pl.edu.pjwstk.byt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("123 Main St", "Warsaw", "Poland", "00-001");
    }

    @Test
    void constructorShouldSetFieldsCorrectly() {
        assertEquals("123 Main St", address.getStreet());
        assertEquals("Warsaw", address.getCity());
        assertEquals("Poland", address.getCountry());
        assertEquals("00-001", address.getZipCode());
    }

    @Test
    void updateAddressShouldChangeFields() {
        address.updateAddress("456 Elm St", "Krakow", "Poland", "31-000");

        assertEquals("456 Elm St", address.getStreet());
        assertEquals("Krakow", address.getCity());
        assertEquals("Poland", address.getCountry());
        assertEquals("31-000", address.getZipCode());
    }

    @Test
    void formatForShippingShouldReturnFormattedString() {
        String expected = "123 Main St, 00-001 Warsaw, Poland";
        assertEquals(expected, address.formatForShipping());

        address.updateAddress("456 Elm St", "Krakow", "Poland", "31-000");
        String expectedUpdated = "456 Elm St, 31-000 Krakow, Poland";
        assertEquals(expectedUpdated, address.formatForShipping());
    }
}
