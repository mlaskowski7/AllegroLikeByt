package pl.edu.pjwstk.byt;

public class Adress {
    private String street;
    private String city;
    private String country;
    private String zipCode;

    public Adress(String street, String city, String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    public void UpdateAdress(String street, String city, String country, String zipCode) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }
    public String formatForShipping() {
        return street + ", " + zipCode + " " + city + ", " + country;
    }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getZipCode() { return zipCode; }
}
