// Adapter Pattern Example
// Existing incompatible class: USDevice
// Target interface: EuropeanPlug
// Adapter: USDeviceAdapter

// Target interface
interface EuropeanPlug {
    void connect();
}

// Adaptee (existing class)
class USDevice {
    void plugInUS() {
        System.out.println("US device plugged in");
    }
}

// Adapter class
class USDeviceAdapter implements EuropeanPlug {

    private USDevice usDevice;

    public USDeviceAdapter(USDevice usDevice) {
        this.usDevice = usDevice;
    }

    @Override
    public void connect() {
        // Translate the call
        usDevice.plugInUS();
        System.out.println("Adapter converts US plug to EU socket");
    }
}

// Main class (client)
public class Adapter {
    public static void main(String[] args) {

        // Existing US device
        USDevice usDevice = new USDevice();

        // Adapter makes it compatible with EuropeanPlug
        EuropeanPlug adapter = new USDeviceAdapter(usDevice);
        adapter.connect();
    }
}
