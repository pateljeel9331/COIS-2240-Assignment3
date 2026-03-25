import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class RentalSystem {
    private static RentalSystem instance;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
        loadData();
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Error: A vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Error: A customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            saveRecord(record);
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            saveRecord(record);
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    private void saveVehicle(Vehicle vehicle) {
        try (FileWriter writer = new FileWriter("vehicles.txt", true)) {
            String type;
            String extra;
            if (vehicle instanceof SportCar sc) {
                type  = "SportCar";
                extra = sc.getNumSeats() + "," + sc.getHorsepower() + "," + sc.hasTurbo();
            } else if (vehicle instanceof Car c) {
                type  = "Car";
                extra = String.valueOf(c.getNumSeats());
            } else if (vehicle instanceof Minibus mb) {
                type  = "Minibus";
                extra = String.valueOf(mb.isAccessible());
            } else if (vehicle instanceof PickupTruck pt) {
                type  = "PickupTruck";
                extra = pt.getCargoSize() + "," + pt.hasTrailer();
            } else {
                type  = "Unknown";
                extra = "";
            }
            writer.write(type + "," +
                         vehicle.getLicensePlate() + "," +
                         vehicle.getMake() + "," +
                         vehicle.getModel() + "," +
                         vehicle.getYear() + "," +
                         vehicle.getStatus() + "," +
                         extra + "\n");
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (FileWriter writer = new FileWriter("customers.txt", true)) {
            writer.write(customer.getCustomerId() + "," +
                         customer.getCustomerName() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {
            writer.write(record.getRecordType() + "," +
                         record.getVehicle().getLicensePlate() + "," +
                         record.getCustomer().getCustomerId() + "," +
                         record.getCustomer().getCustomerName() + "," +
                         record.getRecordDate() + "," +
                         record.getTotalAmount() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                String type   = p[0];
                String plate  = p[1];
                String make   = p[2];
                String model  = p[3];
                int    year   = Integer.parseInt(p[4]);
                Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(p[5]);

                Vehicle vehicle = null;
                switch (type) {
                    case "SportCar" -> {
                        int     numSeats   = Integer.parseInt(p[6]);
                        int     horsepower = Integer.parseInt(p[7]);
                        boolean hasTurbo   = Boolean.parseBoolean(p[8]);
                        vehicle = new SportCar(make, model, year, numSeats, horsepower, hasTurbo);
                    }
                    case "Car" -> {
                        int numSeats = Integer.parseInt(p[6]);
                        vehicle = new Car(make, model, year, numSeats);
                    }
                    case "Minibus" -> {
                        boolean isAccessible = Boolean.parseBoolean(p[6]);
                        vehicle = new Minibus(make, model, year, isAccessible);
                    }
                    case "PickupTruck" -> {
                        double  cargoSize  = Double.parseDouble(p[6]);
                        boolean hasTrailer = Boolean.parseBoolean(p[7]);
                        vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                    }
                }
                if (vehicle != null) {
                    vehicle.setLicensePlate(plate);
                    vehicle.setStatus(status);
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing vehicles data found.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p    = line.split(",");
                int      id   = Integer.parseInt(p[0]);
                String   name = p[1];
                customers.add(new Customer(id, name));
            }
        } catch (IOException e) {
            System.out.println("No existing customers data found.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[]  p          = line.split(",");
                String    recordType = p[0];
                String    plate      = p[1];
                int       customerId = Integer.parseInt(p[2]);
                String    custName   = p[3];
                LocalDate date       = LocalDate.parse(p[4]);
                double    amount     = Double.parseDouble(p[5]);

                Vehicle  vehicle  = findVehicleByPlate(plate);
                Customer customer = findCustomerById(customerId);

                if (customer == null) customer = new Customer(customerId, custName);
                if (vehicle  != null) {
                    rentalHistory.addRecord(
                        new RentalRecord(vehicle, customer, date, amount, recordType));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing rental records data found.");
        }
    }

    // GETTERS FOR GUI
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public List<RentalRecord> getRentalHistory() {
        return rentalHistory.getRentalHistory();
    }
}