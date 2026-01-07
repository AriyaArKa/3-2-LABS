// ChocolateBoiler Singleton Pattern - Thread-Safe Example

public class singeltonChocolateBoiler {

    // ---- State Variables ----
    private boolean empty;
    private boolean boiled;

    // ---- Singleton Instance ----
    private static volatile singeltonChocolateBoiler uniqueInstance;

    // ---- Private Constructor ----
    private singeltonChocolateBoiler() {
        empty = true;   // initially empty
        boiled = false; // initially not boiled
    }

    // ---- Global Access Method (Thread-Safe) ----
    public static singeltonChocolateBoiler getInstance() {
        if (uniqueInstance == null) {             // First check (no locking)
            synchronized (singeltonChocolateBoiler.class) { // Locking
                if (uniqueInstance == null) {      // Second check
                    uniqueInstance = new singeltonChocolateBoiler();
                }
            }
        }
        return uniqueInstance;
    }

    // ---- Fill the Boiler ----
    public void fill() {
        if (empty) {
            empty = false;
            boiled = false;
            System.out.println("Boiler filled with milk/chocolate mixture");
        } else {
            System.out.println("Boiler already filled");
        }
    }

    // ---- Boil the contents ----
    public void boil() {
        if (!empty && !boiled) {
            boiled = true;
            System.out.println("Boiler contents are boiled");
        } else if (empty) {
            System.out.println("Boiler is empty, cannot boil");
        } else {
            System.out.println("Boiler already boiled");
        }
    }

    // ---- Drain the contents ----
    public void drain() {
        if (!empty && boiled) {
            empty = true;
            System.out.println("Boiler drained");
        } else if (empty) {
            System.out.println("Boiler is empty, nothing to drain");
        } else {
            System.out.println("Boiler not boiled yet, cannot drain");
        }
    }

    // ---- Helper Methods ----
    public boolean isEmpty() {
        return empty;
    }

    public boolean isBoiled() {
        return boiled;
    }

    // ---- Main Method to Test ----
    public static void main(String[] args) {

        // Get singleton instance
        singeltonChocolateBoiler boiler = singeltonChocolateBoiler.getInstance();

        // Test the workflow
        boiler.fill();
        boiler.boil();
        boiler.drain();

        // Try again to see messages
        boiler.fill();
        boiler.boil();
        boiler.drain();

        // Get another reference
        singeltonChocolateBoiler anotherBoiler = singeltonChocolateBoiler.getInstance();

        // Verify singleton
        System.out.println("Are both instances same? " + (boiler == anotherBoiler));
    }
}
