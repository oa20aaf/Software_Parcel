import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParcelManagementGUI {
    private QueueofCustomers customerQueue;
    private ParcelMap parcelMap;
    private Log log;
    private Worker worker;

    private DefaultTableModel customerTableModel;
    private DefaultTableModel parcelTableModel;

    public ParcelManagementGUI() {
        customerQueue = new QueueofCustomers();
        parcelMap = new ParcelMap();
        log = new Log();
        worker = new Worker(log);

        JFrame frame = new JFrame("Parcel Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2));
        JPanel logPanel = new JPanel(new BorderLayout());
        JPanel tablePanel = new JPanel(new GridLayout(1, 2));

        JTextField customerNameField = new JTextField();
        JTextField parcelIDField = new JTextField();


        JTextField parcelWeightField = new JTextField();
        JTextField parcelDimensionsField = new JTextField();
        JTextArea logTextArea = new JTextArea(10, 50);
        logTextArea.setEditable(false);

        JLabel customerNameLabel = new JLabel("Customer Name:");
        JLabel parcelIDLabel = new JLabel("Parcel ID:");
        JLabel parcelWeightLabel = new JLabel("Parcel Weight (kg):");
        JLabel parcelDimensionsLabel = new JLabel("Parcel Dimensions:");
//        JButton AddValueButton = new JButton("Add Value");

        JButton addCustomerButton = new JButton("Add Customer");
        JButton addParcelButton = new JButton("Add Parcel");
        JButton removeCustomerButton = new JButton("Remove Customer");
        JButton removeParcelButton = new JButton("Remove Parcel");
        JButton processCustomerButton = new JButton("Process Customer");
        JButton showLogButton = new JButton("Show Log");



        buttonPanel.add(addCustomerButton);
        buttonPanel.add(addParcelButton);
        buttonPanel.add(removeCustomerButton);
        buttonPanel.add(removeParcelButton);
        buttonPanel.add(processCustomerButton);
        buttonPanel.add(showLogButton);

        logPanel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

        customerTableModel = new DefaultTableModel(new String[]{"Sequence No", "Name", "Parcel ID"}, 0);
        JTable customerTable = new JTable(customerTableModel);

        parcelTableModel = new DefaultTableModel(new String[]{"Parcel ID", "Weight", "Dimensions", "Fee", "Status"}, 0);
        JTable parcelTable = new JTable(parcelTableModel);

        tablePanel.add(new JScrollPane(customerTable));
        tablePanel.add(new JScrollPane(parcelTable));

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
//        mainPanel.add(logPanel, BorderLayout.SOUTH);
        mainPanel.add(tablePanel, BorderLayout.EAST);

        frame.add(mainPanel);

        addCustomerButton.addActionListener(e -> {
            JButton AddValueButton = new JButton("Add Value");
            inputPanel.removeAll();
            inputPanel.add(customerNameLabel);
            inputPanel.add(customerNameField);
            inputPanel.add(parcelIDLabel);
            inputPanel.add(parcelIDField);
            inputPanel.add(AddValueButton);
            inputPanel.revalidate();
            inputPanel.repaint();

            AddValueButton.addActionListener(event ->  {
                String name = customerNameField.getText();
                String parcelID = parcelIDField.getText();
                if (!name.isEmpty() && !parcelID.isEmpty()) {
                    int sequenceNo = customerQueue.getQueue().size() + 1;
                    Customer customer = new Customer(sequenceNo, name, parcelID);
                    customerQueue.addCustomer(customer);
                    log.addLogEntry("Added customer: " + name + " with parcel ID: " + parcelID);
                    refreshcustomerTableModel();
                    JOptionPane.showMessageDialog(frame, "Customer added successfully.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields for the customer.");
                }
            });
        });


        addParcelButton.addActionListener(e -> {
            JButton AddValueButton = new JButton("Add Value");
            inputPanel.removeAll();
            inputPanel.add(parcelIDLabel);
            inputPanel.add(parcelIDField);
            inputPanel.add(parcelWeightLabel);
            inputPanel.add(parcelWeightField);
            inputPanel.add(parcelDimensionsLabel);
            inputPanel.add(parcelDimensionsField);
            inputPanel.add(AddValueButton); // A button for confirming the addition
            inputPanel.revalidate();
            inputPanel.repaint();

            AddValueButton.addActionListener(event -> {
                String parcelID = parcelIDField.getText();
                String weightText = parcelWeightField.getText();
                String dimensions = parcelDimensionsField.getText();
                if (!parcelID.isEmpty() && !weightText.isEmpty() && !dimensions.isEmpty()) {
                    try {
                        double weight = Double.parseDouble(weightText); // Validate the weight input
                        Parcel parcel = new Parcel(parcelID, weight, dimensions);
                        parcelMap.addParcel(parcel); // Add the parcel to the parcel map
                        log.addLogEntry("Added parcel: " + parcelID);
                        parcelTableModel.addRow(new Object[]{parcelID, weight, dimensions, parcel.getCollectionFee(), parcel.getStatus()});
                        JOptionPane.showMessageDialog(frame, "Parcel added successfully.");

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid weight format.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields for the parcel.");
                }
            });
        });


        removeCustomerButton.addActionListener(e -> {
            JTextField RemoveparcelIDField = new JTextField();
            JLabel RemoveparcelIDLabel = new JLabel("Parcel ID to Remove Customer:");
            JButton AddValueButton = new JButton("Remove");
            inputPanel.removeAll();
            inputPanel.add(RemoveparcelIDLabel);
            inputPanel.add(RemoveparcelIDField);
            inputPanel.add(AddValueButton);
            inputPanel.revalidate();
            inputPanel.repaint();

            AddValueButton.addActionListener(event -> {

                if (!customerQueue.getQueue().isEmpty()) {
                    Customer c = customerQueue.findCustomerByParcelID(RemoveparcelIDField.getText());
                    if (c != null) {
                        log.addLogEntry("Removed customer: " + c.getName());
                        customerQueue.removeCustomer(c);
                        refreshcustomerTableModel();
                        JOptionPane.showMessageDialog(frame, c.getName() + " Customer removed successfully.");
                    }
                    else {
                        JOptionPane.showMessageDialog(frame, "No customers with parcel ID " +RemoveparcelIDField.getText()+" was found in the queue to remove.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "No customers in the queue to remove.");
                }
            });
        });

        removeParcelButton.addActionListener(e -> {
            String parcelID = parcelIDField.getText();
            if (parcelMap.findParcelByID(parcelID) != null) {
                parcelMap.getAllParcels().remove(parcelID);
                log.addLogEntry("Removed parcel: " + parcelID);
                for (int i = 0; i < parcelTableModel.getRowCount(); i++) {
                    if (parcelTableModel.getValueAt(i, 0).equals(parcelID)) {
                        parcelTableModel.removeRow(i);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Parcel removed successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Parcel not found.");
            }
        });

        processCustomerButton.addActionListener(e -> {
            if (!customerQueue.getQueue().isEmpty()) {
                Customer customer = customerQueue.getQueue().peek();
                worker.processCustomer(customer, parcelMap, customerQueue);
                log.addLogEntry("Processed customer: " + customer.getName());
                customerTableModel.removeRow(0);
                for (int i = 0; i < parcelTableModel.getRowCount(); i++) {
                    if (parcelTableModel.getValueAt(i, 0).equals(customer.getParcelID())) {
                        parcelTableModel.setValueAt("Collected", i, 4);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Customer processed successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "No customers to process.");
            }
        });

        showLogButton.addActionListener(e -> logTextArea.setText(log.getLog()));

        frame.setVisible(true);
    }


    public void refreshcustomerTableModel(){
        for (int i = 0; i < customerTableModel.getRowCount(); i++) {
            customerTableModel.removeRow(i);
        }
        for (Customer c: customerQueue.getQueue()) {
            customerTableModel.addRow(new Object[]{c.getSequenceNo(),c.getName(), c.getParcelID()});
        }

    }

    public static void main(String[] args) {
        new ParcelManagementGUI();
    }
}
