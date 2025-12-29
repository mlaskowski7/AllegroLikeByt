package pl.edu.pjwstk.byt;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private String street;
    private String city;
    private String country;
    private String zipCode;

    public Address(String street, String city, String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    public void updateAddress(String street, String city, String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    /**
     * @deprecated Use {@link #updateAddress(String, String, String, String)} instead.
     */
    @Deprecated
    public void UpdateAdress(String street, String city, String country, String zipCode) {
        updateAddress(street, city, country, zipCode);
    }

    public String formatForShipping() {
        return street + ", " + zipCode + " " + city + ", " + country;
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getZipCode() { return zipCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(country, address.country) &&
               Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, country, zipCode);
    }
}
