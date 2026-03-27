import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleRentalTest {

    private RentalSystem system;

    @BeforeEach
    void setUp() {
        system = RentalSystem.getInstance();
    }

    // -------------------------------------------------------------------------
    // Task: License Plate Validation
    // -------------------------------------------------------------------------
    @Test
    void testLicensePlate() {
        // --- Valid plates: should NOT throw ---
        Car car1 = new Car("Toyota", "Corolla", 2020, 5);
        assertDoesNotThrow(() -> car1.setLicensePlate("AAA100"));
        assertEquals("AAA100", car1.getLicensePlate());

        Car car2 = new Car("Honda", "Civic", 2021, 5);
        assertDoesNotThrow(() -> car2.setLicensePlate("ABC567"));
        assertEquals("ABC567", car2.getLicensePlate());

        Car car3 = new Car("Ford", "Focus", 2022, 5);
        assertDoesNotThrow(() -> car3.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", car3.getLicensePlate());

        // --- Invalid plates: should throw IllegalArgumentException ---

        // Empty string
        Car invalidCar1 = new Car("Mazda", "3", 2023, 5);
        assertThrows(IllegalArgumentException.class, () -> invalidCar1.setLicensePlate(""));

        // Null
        Car invalidCar2 = new Car("Nissan", "Altima", 2023, 5);
        assertThrows(IllegalArgumentException.class, () -> invalidCar2.setLicensePlate(null));

        // Too many digits (AAA1000 — 3 letters + 4 digits)
        Car invalidCar3 = new Car("Chevrolet", "Malibu", 2020, 5);
        assertThrows(IllegalArgumentException.class, () -> invalidCar3.setLicensePlate("AAA1000"));

        // Too few digits (ZZZ99 — 3 letters + 2 digits)
        Car invalidCar4 = new Car("Hyundai", "Elantra", 2021, 5);
        assertThrows(IllegalArgumentException.class, () -> invalidCar4.setLicensePlate("ZZZ99"));
    }

    // -------------------------------------------------------------------------
    // Task: Rent and Return Vehicle Validation
    // -------------------------------------------------------------------------
    @Test
    void testRentAndReturnVehicle() {
        // Use a unique plate to avoid collisions with persisted data
        Car car = new Car("Toyota", "Corolla", 2020, 5);
        car.setLicensePlate("TST001");

        Customer customer = new Customer(9001, "TestUser");

        // Vehicle should be available before any action
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        // Add to system (may already exist if tests run twice; find or add)
        Vehicle existing = system.findVehicleByPlate("TST001");
        if (existing == null) {
            system.addVehicle(car);
        } else {
            car = (Car) existing;
            car.setStatus(Vehicle.VehicleStatus.Available); // reset state
        }

        Customer existingCustomer = system.findCustomerById(9001);
        if (existingCustomer == null) {
            system.addCustomer(customer);
        } else {
            customer = existingCustomer;
        }

        // --- Rent successfully ---
        boolean rentResult = system.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertTrue(rentResult, "First rent should succeed");
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus(), "Vehicle should be RENTED after renting");

        // --- Try renting the same vehicle again — should fail ---
        boolean rentAgain = system.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertFalse(rentAgain, "Renting an already-rented vehicle should fail");

        // --- Return successfully ---
        boolean returnResult = system.returnVehicle(car, customer, LocalDate.now(), 0.0);
        assertTrue(returnResult, "Return should succeed");
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus(), "Vehicle should be AVAILABLE after return");

        // --- Try returning the same vehicle again — should fail ---
        boolean returnAgain = system.returnVehicle(car, customer, LocalDate.now(), 0.0);
        assertFalse(returnAgain, "Returning an already-available vehicle should fail");
    }

    // -------------------------------------------------------------------------
    // Task: Singleton Validation
    // -------------------------------------------------------------------------
    @Test
    void testSingletonRentalSystem() throws Exception {
        // Verify the constructor is private
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                   "RentalSystem constructor should be private");

        // Verify getInstance() returns a non-null instance
        RentalSystem r1 = RentalSystem.getInstance();
        assertNotNull(r1, "getInstance() should not return null");

        // Verify the same instance is returned every time
        RentalSystem r2 = RentalSystem.getInstance();
        assertSame(r1, r2, "getInstance() should always return the same instance");
    }
}