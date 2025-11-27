package Backend;

public class TestFleet {
    public static void main(String[] args) {
        FleetService fs = new FleetService();

        System.out.println("Initial fleet size: " + fs.getFleet().size());

        Car c = new Car("MH01CG9394","Toyota","5");
        fs.addVehicle(c);

        System.out.println("After add, fleet size: " + fs.getFleet().size());

        for (Vehicle v : fs.getFleet()) {
            System.out.println("Fleet item: " + v.toString());
        }

        System.out.println("Search MH01CG9394: " + (fs.searchVehicle("MH01CG9394") != null));
        System.out.println("Rent MH01CG9394: " + fs.rentVehicle("MH01CG9394"));
        System.out.println("Rent MH01CG9394 again: " + fs.rentVehicle("MH01CG9394"));
        System.out.println("Return MH01CG9394: " + fs.returnVehicle("MH01CG9394"));
    }
}
