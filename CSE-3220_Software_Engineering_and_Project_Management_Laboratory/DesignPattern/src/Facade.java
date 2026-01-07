// Facade Pattern Example
// Client uses a single interface to start a complex subsystem

// Subsystem classes
class CPU {
    void start() {
        System.out.println("CPU starts");
    }
}

class Memory {
    void load() {
        System.out.println("Memory loads");
    }
}

class HardDrive {
    void read() {
        System.out.println("HardDrive reads data");
    }
}

// Facade class
class ComputerFacade {

    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;

    public ComputerFacade() {
        cpu = new CPU();
        memory = new Memory();
        hardDrive = new HardDrive();
    }

    // Simplified method for client
    public void startComputer() {
        System.out.println("Starting computer...");
        cpu.start();
        memory.load();
        hardDrive.read();
        System.out.println("Computer started successfully!");
    }
}

// Main class (Client)
public class Facade {
    public static void main(String[] args) {

        // Client uses facade, not subsystems directly
        ComputerFacade computer = new ComputerFacade();
        computer.startComputer();
    }
}
