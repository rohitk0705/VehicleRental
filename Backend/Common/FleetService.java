package Backend.Common;

import java.io.*;
import java.util.*;

public class FleetService {
    private List<Vehicle> fleet = new ArrayList<>();
    private File dataFile;
    private File revenueFile;
    private double totalRevenue = 0.0;

    public FleetService(File dataFile){
        this.dataFile = dataFile;
        this.revenueFile = new File("revenue.txt");
        loadFleet();
        loadRevenue();
    }

    public FleetService() {
        this(new File("fleet.txt"));
    }

    public List<Vehicle> getFleet(){ return fleet; }

    public double getTotalRevenue(){ return totalRevenue; }

    public boolean existsId(String id){
        return fleet.stream().anyMatch(v -> v.getId().equals(id));
    }

    public void addVehicle(Vehicle v){
        fleet.add(v);
        saveFleet();
    }

    public Vehicle searchVehicle(String id){
        return fleet.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    public String rentVehicle(String id){
        Vehicle v = searchVehicle(id);
        if (v == null) return "Vehicle not found.";

        if (!(v instanceof Rentable)) return "Vehicle not rentable.";

        try {
            ((Rentable)v).rent();
            v.incrementRentalCount();
            addRevenue(v.getPrice());
            saveFleet();
            return v.getTypeName() + " " + id + " rented.";
        } catch (VehicleNotAvailableException e){
            return e.getMessage();
        }
    }

    public String returnVehicle(String id){
        Vehicle v = searchVehicle(id);

        if (v == null) return "Vehicle not found.";
        if (!(v instanceof Rentable)) return "Vehicle not rentable.";

        ((Rentable)v).returnVehicle();
        saveFleet();
        return v.getTypeName() + " " + id + " returned.";
    }

    public boolean deleteVehicle(String id) {
        Vehicle v = searchVehicle(id);
        if (v != null) {
            fleet.remove(v);
            saveFleet();
            return true;
        }
        return false;
    }

    public boolean updateVehicle(String id, String brand, String extra, double price) {
        Vehicle v = searchVehicle(id);
        if (v == null) return false;

        v.setBrand(brand);
        v.setPrice(price);

        if (v instanceof Car) {
            ((Car) v).setSeats(extra);
        } else if (v instanceof Bike) {
            ((Bike) v).setBikeType(extra);
        } else if (v instanceof Truck) {
            try {
                ((Truck) v).setLoadCapacity(Double.parseDouble(extra));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        saveFleet();
        return true;
    }

    public void clearData(){
        fleet.clear();
        saveFleet();
    }

    public void loadTestData() {
        if (!existsId("MH01CG9394")) addVehicle(new Car("MH01CG9394", "Toyota", "5", 50.0));
        if (!existsId("MH02AB1234")) addVehicle(new Bike("MH02AB1234", "Honda", "Sports", 20.0));
        if (!existsId("MH03XY9876")) addVehicle(new Truck("MH03XY9876", "Tata", 1000.0, 150.0));
        saveFleet();
    }

    public void importCsv(File f) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while((line = br.readLine()) != null){
                String[] p = line.split(",");
                if (p.length < 6) continue;

                String type = p[0];
                String id = p[1];
                String brand = p[2];
                boolean rented = Boolean.parseBoolean(p[3]);
                String extra = p[4];
                double price = Double.parseDouble(p[5]);
                int rentalCount = 0;
                if (p.length > 6) {
                    try {
                        rentalCount = Integer.parseInt(p[6]);
                    } catch (NumberFormatException e) {
                        rentalCount = 0;
                    }
                }

                if (existsId(id)) continue;

                Vehicle v = null;

                switch (type) {
                    case "Car": v = new Car(id, brand, extra, price); break;
                    case "Bike": v = new Bike(id, brand, extra, price); break;
                    case "Truck": v = new Truck(id, brand, Double.parseDouble(extra), price); break;
                }

                if (v != null) {
                    v.setRented(rented);
                    v.setRentalCount(rentalCount);
                    fleet.add(v);
                }
            }
        }
        saveFleet();
    }

    public void exportCsv(File f) throws IOException{
        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Vehicle v : fleet) {
                pw.println(
                    v.getTypeName() + "," +
                    v.getId() + "," +
                    v.getBrand() + "," +
                    v.isRented() + "," +
                    v.getExtra() + "," +
                    v.getPrice() + "," +
                    v.getRentalCount()
                );
            }
        }
    }

    private void addRevenue(double amount) {
        if (amount <= 0) return;
        totalRevenue += amount;
        saveRevenue();
    }

    private void loadRevenue() {
        totalRevenue = 0;
        if (!revenueFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(revenueFile))) {
            String line = br.readLine();
            if (line != null) {
                totalRevenue = Double.parseDouble(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            totalRevenue = 0;
        }
    }

    private void saveRevenue() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(revenueFile))) {
            pw.println(totalRevenue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFleet(){
        if (!dataFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;

            while((line = br.readLine()) != null){
                String[] p = line.split(",");
                if (p.length < 6) continue;

                String type = p[0];
                String id = p[1];
                String brand = p[2];
                boolean rented = Boolean.parseBoolean(p[3]);
                String extra = p[4];
                double price = Double.parseDouble(p[5]);
                int rentalCount = 0;
                if (p.length > 6) {
                    try {
                        rentalCount = Integer.parseInt(p[6]);
                    } catch (NumberFormatException e) {
                        rentalCount = 0;
                    }
                }

                Vehicle v = null;

                switch (type) {
                    case "Car": v = new Car(id, brand, extra, price); break;
                    case "Bike": v = new Bike(id, brand, extra, price); break;
                    case "Truck": v = new Truck(id, brand, Double.parseDouble(extra), price); break;
                }

                if (v != null) {
                    v.setRented(rented);
                    v.setRentalCount(rentalCount);
                    fleet.add(v);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFleet(){
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataFile))) {
            for (Vehicle v : fleet) {
                pw.println(
                    v.getTypeName() + "," +
                    v.getId() + "," +
                    v.getBrand() + "," +
                    v.isRented() + "," +
                    v.getExtra() + "," +
                    v.getPrice() + "," +
                    v.getRentalCount()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
