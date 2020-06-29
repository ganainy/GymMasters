package ganainy.dev.gymmasters.models.app_models;

public class Gym {
    private String name;
    private Double rate;
    private String address;
    private String openingHours;

    public Gym(String name, Double rate, String address, String openingHours) {
        this.name = name;
        this.rate = rate;
        this.address = address;
        this.openingHours = openingHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
}
