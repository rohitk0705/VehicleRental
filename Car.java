package VehicleRental;

public class Car extends Vehicle implements Rentable {
    private String seats;

    public Car(String id, String brand, String seats){
        super(id, brand);
        this.seats = seats;
    }

    public String getSeats(){ return seats; }

    @Override
    public void rent() throws VehicleNotAvailableException {
        if (isRented())
            throw new VehicleNotAvailableException("Car " + getId() + " is already rented.");
        setRented(true);
    }

    @Override
    public void returnVehicle(){
        setRented(false);
    }

    @Override
    public String getTypeName(){ return "Car"; }

    @Override
    public String getExtra(){ return seats; }
}
