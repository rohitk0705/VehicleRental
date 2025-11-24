package VehicleRental;

public class Truck extends Vehicle implements Rentable {
    private double loadCapacity;

    public Truck(String id, String brand, double loadCapacity){
        super(id, brand);
        this.loadCapacity = loadCapacity;
    }

    @Override
    public void rent() throws VehicleNotAvailableException {
        if (isRented())
            throw new VehicleNotAvailableException("Truck " + getId() + " is already rented.");
        setRented(true);
    }

    @Override
    public void returnVehicle(){
        setRented(false);
    }

    @Override
    public String getTypeName(){ return "Truck"; }

    @Override
    public String getExtra(){ return Double.toString(loadCapacity); }

    public double getLoadCapacity(){ return loadCapacity; }
}
