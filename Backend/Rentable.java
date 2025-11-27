package Backend;

public interface Rentable {
    void rent() throws VehicleNotAvailableException;
    void returnVehicle();
}
