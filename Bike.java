package VehicleRental;

public class Bike extends Vehicle implements Rentable {
    private String bikeType;

    public Bike(String id, String brand, String bikeType){
        super(id, brand);
        this.bikeType = bikeType;
    }

    @Override
    public void rent() throws VehicleNotAvailableException {
        if (isRented())
            throw new VehicleNotAvailableException("Bike " + getId() + " is already rented.");
        setRented(true);
    }

    @Override
    public void returnVehicle(){
        setRented(false);
    }

    @Override
    public String getTypeName(){ return "Bike"; }

    @Override
    public String getExtra(){ return bikeType; }

    public String getType(){ return bikeType; }
}
