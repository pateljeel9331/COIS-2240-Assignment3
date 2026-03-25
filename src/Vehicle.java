public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
        this.make  = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return null;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public Vehicle() {
        this(null, null, 0);
    }

    private boolean isValidPlate(String plate) {
        if (plate == null) return false;
        return plate.matches("[A-Z]{3}[0-9]{3}");
    }

    public void setLicensePlate(String plate) {
        String formattedPlate = plate == null ? null : plate.toUpperCase();
        if (!isValidPlate(formattedPlate)) {
            throw new IllegalArgumentException("Invalid license plate");
        }
        this.licensePlate = formattedPlate;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model; }

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
}