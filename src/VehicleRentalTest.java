import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class VehicleRentalTest {

    @Test
void testLicensePlate() {
Car validCar = new Car("Toyota", "Corolla", 2020, 5);

assertDoesNotThrow(() -> validCar.setLicensePlate("ABC123"));
assertEquals("ABC123", validCar.getLicensePlate());

Car invalidCar1 = new Car("Honda", "Civic", 2021, 5);
assertThrows(IllegalArgumentException.class, () -> {
invalidCar1.setLicensePlate("123ABC");
        });

Car invalidCar2 = new Car("Ford", "Focus", 2022, 5);
assertThrows(IllegalArgumentException.class, () -> {
invalidCar2.setLicensePlate("AB1234");
        });

Car invalidCar3 = new Car("Mazda", "3", 2023, 5);
assertThrows(IllegalArgumentException.class, () -> {
invalidCar3.setLicensePlate("AAAA11");
        });
    }

    @Test
void testRentAndReturnVehicle() {
RentalSystem system = RentalSystem.getInstance();

Car car = new Car("Toyota", "Corolla", 2020, 5);
car.setLicensePlate("TES123");

Customer customer = new Customer(999, "TestCustomer");

system.addVehicle(car);
system.addCustomer(customer);

system.rentVehicle(car, customer, LocalDate.now(), 100.0);
assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());

system.returnVehicle(car, customer, LocalDate.now(), 0.0);
assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
    }

    @Test
void testSingletonRentalSystem() throws Exception {
Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
assertTrue(Modifier.isPrivate(constructor.getModifiers()));

RentalSystem r1 = RentalSystem.getInstance();
RentalSystem r2 = RentalSystem.getInstance();

assertSame(r1, r2);
    }
}