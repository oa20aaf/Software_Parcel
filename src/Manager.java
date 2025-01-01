import java.text.ParseException;
import java.util.Scanner;

public class Manager {
    public static void main(String[] args) {
        // Initialize components
        ParcelMap parcelMap = new ParcelMap();
        QueueofCustomers queue = new QueueofCustomers();
        Log log = new Log();
        Worker worker = new Worker(log);

        // Example data

        parcelMap.addParcel(new Parcel("P001", 10, "125", "01/01/2024"));
        parcelMap.addParcel(new Parcel("P002", 5, "100", "02/01/2024"));

        queue.addCustomer(new Customer(1, "Alice", "John", "P001"));
        queue.addCustomer(new Customer(2, "Bob", "Mathew", "P002"));

        // Command-line interface
        Scanner scanner = new Scanner(System.in);
        while (!queue.isEmpty()) {
            System.out.println("\nNext customer: " + queue.getQueue().peek());
            System.out.println("Enter 'process' to process customer or 'log' to view log:");
            String input = scanner.nextLine();

            if ("process".equalsIgnoreCase(input)) {
                worker.processCustomer(queue.getQueue().peek(), parcelMap, queue);
            } else if ("log".equalsIgnoreCase(input)) {
                System.out.println("Log:\n" + log.getLog());
            } else {
                System.out.println("Invalid input.");
            }
        }

        System.out.println("All customers processed.");
        System.out.println("Final Log:\n" + log.getLog());
        scanner.close();
    }
}
