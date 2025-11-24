package VehicleRental;

public interface Rentable {
    void rent() throws VehicleNotAvailableException;
    void returnVehicle();
}
