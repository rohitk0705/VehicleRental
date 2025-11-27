package Backend;

import java.io.*;
import java.util.*;

public class FleetService {
    private List<Vehicle> fleet = new ArrayList<>();
    private File dataFile;

    public FleetService(File dataFile){
        this.dataFile = dataFile;
        loadFleet();
    }

    public FleetService() {
        this(new File("fleet.txt"));
    }

    public List<Vehicle> getFleet(){ return fleet; }

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

    public void clearData(){
        fleet.clear();
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

                if (existsId(id)) continue;

                Vehicle v = null;

                switch (type) {
                    case "Car": v = new Car(id, brand, extra, price); break;
                    case "Bike": v = new Bike(id, brand, extra, price); break;
                    case "Truck": v = new Truck(id, brand, Double.parseDouble(extra), price); break;
                }

                if (v != null) {
                    v.setRented(rented);
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
                    v.getPrice()
                );
            }
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

                Vehicle v = null;

                switch (type) {
                    case "Car": v = new Car(id, brand, extra, price); break;
                    case "Bike": v = new Bike(id, brand, extra, price); break;
                    case "Truck": v = new Truck(id, brand, Double.parseDouble(extra), price); break;
                }

                if (v != null) {
                    v.setRented(rented);
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
                    v.getPrice()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
