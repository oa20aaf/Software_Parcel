import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class QueueofCustomers {
    private Queue<Customer> queue;

    public QueueofCustomers() {
        this.queue = new LinkedList<>();
    }

    public void addCustomer(Customer customer) {
        queue.add(customer);
    }

    public void removeCustomer(Customer c) {
        // Using an iterator to safely remove items during iteration
        Iterator<Customer> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Customer customer = iterator.next();
            if (customer == c) {
                iterator.remove(); // Safely remove the customer from the queue
                break; // Exit the loop once the customer is removed
            }
        }
    }


    public Customer findCustomerByParcelID(String parcelID) {
        for (Customer customer : queue) {
            if (customer.getParcelID().equals(parcelID)) {
                return customer;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Queue<Customer> getQueue() {
        return queue;
    }
}
