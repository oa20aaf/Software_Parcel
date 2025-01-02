public class Worker {
    private Log log;

    public Worker(Log log) {
        this.log = log;
    }

    public void processCustomer(Customer customer, ParcelMap parcelMap, QueueofCustomers queue) {
        Parcel parcel = parcelMap.findParcelByID(customer.getParcelID());
        if (parcel != null && "Waiting".equals(parcel.getStatus())) {
            parcel.setStatus("Collected");
            queue.removeCustomer(customer);
        } else {
            log.addLogEntry("Error: Parcel not found or already collected for customer: " + customer.getName());
        }
    }
}
