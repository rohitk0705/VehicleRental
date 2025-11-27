package Backend.Common;

public class Vehicle {
    private String id;
    private String brand;
    private boolean rented;
    private double price;
    private int rentalCount;

    public Vehicle(String id, String brand, double price) {
        this.id = id;
        this.brand = brand;
        this.price = price;
        this.rented = false;
        this.rentalCount = 0;
    }

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isRented() { return rented; }
    public void setRented(boolean r) { this.rented = r; }
    public int getRentalCount() { return rentalCount; }
    public void setRentalCount(int c) { this.rentalCount = c; }
    public void incrementRentalCount() { this.rentalCount++; }

    public String getTypeName() { return "Vehicle"; }
    public String getExtra() { return ""; }

    @Override
    public String toString() {
        return getTypeName() + " [ID=" + id +
               ", Brand=" + brand +
               ", Rented=" + rented +
               ", Extra=" + getExtra() + "]";
    }
}