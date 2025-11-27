package Backend.Common;

public class Car extends Vehicle implements Rentable {
    private String seats;

    public Car(String id, String brand, String seats, double price){
        super(id, brand, price);
        this.seats = seats;
    }

    public String getSeats(){ return seats; }
    public void setSeats(String seats) { this.seats = seats; }

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
