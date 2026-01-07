// Factory Pattern Example
// Example: Vehicle Factory

// Product Interface
interface Vehicle {
    void drive();
}

// Concrete Products
class Car implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Driving a Car");
    }
}

class Bike implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Riding a Bike");
    }
}

// Factory Class
class VehicleFactory {

    public static Vehicle getVehicle(String type) {

        if (type == null) {
            return null;
        }

        if (type.equalsIgnoreCase("car")) {
            return new Car();
        }
        else if (type.equalsIgnoreCase("bike")) {
            return new Bike();
        }

        return null;
    }
}

// Main Class (Client)
public class Factory {
    public static void main(String[] args) {

        // Client does not use 'new' directly
        Vehicle vehicle1 = VehicleFactory.getVehicle("car");
        vehicle1.drive();

        Vehicle vehicle2 = VehicleFactory.getVehicle("bike");
        vehicle2.drive();
    }
}
