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

    public Customer removeCustomer() {
        return queue.poll();
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
