package Backend.Common;

public interface Rentable {
    void rent() throws VehicleNotAvailableException;
    void returnVehicle();
}
