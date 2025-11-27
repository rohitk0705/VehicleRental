package Backend;

public class Vehicle {
    private String id;
    private String brand;
    private boolean rented;

    public Vehicle(String id, String brand) {
        this.id = id;
        this.brand = brand;
        this.rented = false;
    }

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public boolean isRented() { return rented; }
    public void setRented(boolean r) { this.rented = r; }

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