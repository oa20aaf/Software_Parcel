import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

// JTextField components
        JTextField customerNameField = new JTextField();
        JTextField customerLastNameField = new JTextField();
        JTextField parcelIDField = new JTextField();
        JTextField parcelWeightField = new JTextField();
        JTextField parcelDimensionsField = new JTextField();
        JTextArea logTextArea = new JTextArea(10, 50);
        logTextArea.setEditable(false);

// JLabel components
        JLabel customerNameLabel = new JLabel("Customer Name:");
        JLabel customerLastNameLabel = new JLabel("Customer Last Name:");
        JLabel parcelIDLabel = new JLabel("Parcel ID:");
        JLabel parcelWeightLabel = new JLabel("Parcel Weight (kg):");
        JLabel parcelDimensionsLabel = new JLabel("Parcel Dimensions:");

// JButton components
        JButton addCustomerButton = new JButton("Add Customer");
        JButton addParcelButton = new JButton("Add Parcel");
        JButton removeCustomerButton = new JButton("Remove Customer");
        JButton removeParcelButton = new JButton("Remove Parcel");
        JButton processCustomerButton = new JButton("Process Customer");
        JButton showLogButton = new JButton("Show Log");
        JButton importParcels = new JButton("Import Parcels");
        JButton exportParcels = new JButton("Export Parcels");
        JButton importCustomerButton = new JButton("Import Customers");
        JButton exportCustomerButton = new JButton("Export Customers");

// Add buttons to the button panel
        buttonPanel.add(addCustomerButton);
        buttonPanel.add(addParcelButton);
        buttonPanel.add(removeCustomerButton);
        buttonPanel.add(removeParcelButton);
        buttonPanel.add(processCustomerButton);
        buttonPanel.add(showLogButton);
        buttonPanel.add(importParcels);
        buttonPanel.add(exportParcels);
        buttonPanel.add(importCustomerButton);
        buttonPanel.add(exportCustomerButton);

// Add the log text area to the log panel
        logPanel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

// Table models and components
        DefaultTableModel customerTableModel = new DefaultTableModel(new String[]{"Sequence No", "First Name","Last Name", "Parcel ID"}, 0);
        JTable customerTable = new JTable(customerTableModel);

        DefaultTableModel parcelTableModel = new DefaultTableModel(new String[]{"Parcel ID", "Weight", "Dimensions", "Fee", "Status", "Received Day", "Days Waiting"}, 0);
        JTable parcelTable = new JTable(parcelTableModel);

// Add tables to the table panel
        tablePanel.add(new JScrollPane(customerTable));
        tablePanel.add(new JScrollPane(parcelTable));

// Set the preferred size of the input panel (make it smaller in height)
        inputPanel.setPreferredSize(new Dimension(400, 150)); // Adjust the height here

// Allow table panel to expand and take more space
        tablePanel.setPreferredSize(new Dimension(800, 600)); // Adjust width and height as needed

// Add panels to the main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER); // Changed from EAST to CENTER for better layout

// Add the main panel to the frame
        frame.add(mainPanel);


        addCustomerButton.addActionListener(e -> {
            JButton AddValueButton = new JButton("Add Value");
            inputPanel.removeAll();
            inputPanel.add(customerNameLabel);
            inputPanel.add(customerNameField);
            inputPanel.add(customerLastNameLabel);
            inputPanel.add(customerLastNameField);
            inputPanel.add(parcelIDLabel);
            inputPanel.add(parcelIDField);
            inputPanel.add(AddValueButton);
            inputPanel.revalidate();
            inputPanel.repaint();

            AddValueButton.addActionListener(event -> {
                String name = customerNameField.getText();
                String lastName = customerLastNameField.getText();
                String parcelID = parcelIDField.getText();

                if (!name.isEmpty() && !parcelID.isEmpty()) {
                    // Check if the parcelID exists in ParcelMap
                    Parcel parcel = parcelMap.findParcelByID(parcelID);

                    if (parcel != null) {
                        // Parcel exists in the ParcelMap, so add the customer
                        int sequenceNo = customerQueue.getQueue().size() + 1;
                        Customer customer = new Customer(sequenceNo, name, lastName, parcelID);
                        customerQueue.addCustomer(customer);
                        customerTableModel.addRow(new Object[]{customer.getSequenceNo(),customer.getName(),customer.getLastname(), customer.getParcelID()});
                        log.addLogEntry("Added customer: " + name + " " + lastName + " with parcel ID: " + parcelID);
                        JOptionPane.showMessageDialog(frame, "Customer added successfully.");
                    } else {
                        // Parcel ID does not exist in the ParcelMap
                        JOptionPane.showMessageDialog(frame, "The provided Parcel ID does not exist.");
                    }
                } else {
                    // Fields are empty
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields for the customer.");
                }
            });
        });


        addParcelButton.addActionListener(e -> {
            JButton AddValueButton = new JButton("Add Parcel");

            // Date Input Field
            JLabel dateLabel = new JLabel("Date Received (DD/MM/YYYY):");
            JTextField dateField = new JTextField();

            inputPanel.removeAll();
            inputPanel.add(parcelWeightLabel);
            inputPanel.add(parcelWeightField);
            inputPanel.add(parcelDimensionsLabel);
            inputPanel.add(parcelDimensionsField);
            inputPanel.add(dateLabel);
            inputPanel.add(dateField); // Add the date input field
            inputPanel.add(AddValueButton); // Add the button to confirm parcel addition
            inputPanel.revalidate();
            inputPanel.repaint();

            AddValueButton.addActionListener(event -> {
                String weightText = parcelWeightField.getText();
                String dimensions = parcelDimensionsField.getText();
                String dateReceived = dateField.getText();

                if (!weightText.isEmpty() && !dimensions.isEmpty() && !dateReceived.isEmpty()) {
                    try {
                        // Validate and format the date input
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        dateFormat.setLenient(false); // Strict date validation
                        dateFormat.parse(dateReceived); // This will throw an exception if the date is invalid

                        // Generate the next parcelID (e.g., P001, P002, P003)
                        String newParcelID = generateNewParcelID();

                        // Calculate the difference in days
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate receivedDate = LocalDate.parse(dateReceived, formatter);
                        LocalDate currentDate = LocalDate.now();
                        long daysDifference = ChronoUnit.DAYS.between(receivedDate, currentDate);

                        double weight = Double.parseDouble(weightText); // Validate the weight input
                        Parcel parcel = new Parcel(newParcelID, weight, dimensions, dateReceived);
                        parcelMap.addParcel(parcel); // Add the parcel to the parcel map
                        log.addLogEntry("Added parcel: " + newParcelID);

                        parcelTableModel.addRow(new Object[]{
                                newParcelID, weight, dimensions, parcel.calculateCollectionFee(dateReceived), parcel.getStatus(), dateReceived, daysDifference
                        });

                        JOptionPane.showMessageDialog(frame, "Parcel added successfully.");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid weight format.");
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid date format. Use DD/MM/YYYY.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields for the parcel.");
                }
            });
        });



        removeCustomerButton.addActionListener(e -> {
            // Prompt the user to input the Parcel ID to find the associated customer
            String parcelIDToRemove = JOptionPane.showInputDialog(frame, "Enter the Parcel ID of the customer to remove:");

            if (parcelIDToRemove != null && !parcelIDToRemove.isEmpty()) {
                // Find the customer by parcelID from the customerQueue
                Customer customerToRemove = null;
                for (Customer customer : customerQueue.getQueue()) {
                    if (customer.getParcelID().equals(parcelIDToRemove)) {
                        customerToRemove = customer;
                        break;
                    }
                }

                if (customerToRemove != null) {
                    // Remove the customer from the customerQueue
                    customerQueue.removeCustomer(customerToRemove);

                    // Remove the row from the customer table model
                    for (int i = 0; i < customerTableModel.getRowCount(); i++) {
                        String parcelID = (String) customerTableModel.getValueAt(i, 3); // Assuming Parcel ID is in the 4th column
                        if (parcelID.equals(parcelIDToRemove)) {
                            customerTableModel.removeRow(i);
                            break;
                        }
                    }

                    // Show confirmation message
                    JOptionPane.showMessageDialog(frame, "Customer with Parcel ID " + parcelIDToRemove + " removed successfully.");
                } else {
                    // Show error message if the customer is not found
                    JOptionPane.showMessageDialog(frame, "Customer with Parcel ID " + parcelIDToRemove + " not found.");
                }
            } else {
                // Show error message if no Parcel ID was entered
                JOptionPane.showMessageDialog(frame, "Please enter a valid Parcel ID.");
            }
        });



        // Add ActionListener to removeParcelButton (assuming you have a removeParcelButton in your UI)
        removeParcelButton.addActionListener(e -> {
            // Prompt the user to input the Parcel ID to remove
            String parcelIDToRemove = JOptionPane.showInputDialog(frame, "Enter the Parcel ID to remove:");

            if (parcelIDToRemove != null && !parcelIDToRemove.isEmpty()) {
                // Check if the parcel exists in the parcelMap
                Parcel parcelToRemove = parcelMap.findParcelByID(parcelIDToRemove);

                if (parcelToRemove != null) {
                    // Remove the parcel from the parcelMap
                    parcelMap.removeParcel(parcelIDToRemove);

                    // Remove the row from the table model
                    for (int i = 0; i < parcelTableModel.getRowCount(); i++) {
                        String parcelID = (String) parcelTableModel.getValueAt(i, 0); // Assuming Parcel ID is in the first column
                        if (parcelID.equals(parcelIDToRemove)) {
                            parcelTableModel.removeRow(i);
                            break;
                        }
                    }

                    // Show confirmation message
                    JOptionPane.showMessageDialog(frame, "Parcel " + parcelIDToRemove + " removed successfully.");
                } else {
                    // Show error message if the parcel is not found
                    JOptionPane.showMessageDialog(frame, "Parcel with ID " + parcelIDToRemove + " not found.");
                }
            } else {
                // Show error message if no Parcel ID was entered
                JOptionPane.showMessageDialog(frame, "Please enter a valid Parcel ID.");
            }
        });

        processCustomerButton.addActionListener(e -> {
            inputPanel.removeAll();
            inputPanel.add(parcelIDLabel);
            inputPanel.add(parcelIDField);
            inputPanel.add(parcelWeightLabel);
            inputPanel.add(parcelWeightField);
            inputPanel.add(parcelDimensionsLabel);
            inputPanel.add(parcelDimensionsField);
            inputPanel.revalidate();
            inputPanel.repaint();
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

        importParcels.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 7) { // Ensure the file format is correct
                            String parcelID = parts[0];
                            double weight = Double.parseDouble(parts[1]);
                            String dimensions = parts[2];
                            double collectionFee = Double.parseDouble(parts[3]);
                            String status = parts[4];
                            String dateReceived = parts[5];
                            long daysDifference = Long.parseLong(parts[6]);

                            // Create a Parcel object
                            Parcel parcel = new Parcel(parcelID, weight, dimensions, dateReceived);
                            parcel.setStatus(status);

                            // Add to parcelMap
                            parcelMap.addParcel(parcel);

                            // Add to table model
                            parcelTableModel.addRow(new Object[]{
                                    parcelID, weight, dimensions, collectionFee, status, dateReceived, daysDifference
                            });
                        }
                    }

                    JOptionPane.showMessageDialog(frame, "Data imported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading the file: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Error in file data format: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "File selection cancelled.");
            }
        });


        // Action Listener for Export Button
        exportParcels.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                    for (Parcel parcel : parcelMap.getAllParcels().values()) {
                        writer.write(String.format("%s,%.2f,%s,%.2f,%s,%s,%d%n",
                                parcel.getParcelID(),
                                parcel.getWeight(),
                                parcel.getDimensions(),
                                parcel.getCollectionFee(),
                                parcel.getStatus(),
                                parcel.getDateReceived(),
                                parcel.getDateDiff(parcel.getDateReceived())));
                    }
                    JOptionPane.showMessageDialog(frame, "Parcels exported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error exporting parcels: " + ex.getMessage());
                }
            }
        });


        exportCustomerButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (Customer customer : customerQueue.getQueue()) {
                        // Writing customer details to the CSV file
                        writer.write(customer.getSequenceNo() + "," + customer.getName() + "," + customer.getLastname() + "," + customer.getParcelID());
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(frame, "Customers exported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error exporting customers: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Export cancelled.");
            }
        });

        importCustomerButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            int sequenceNo = Integer.parseInt(parts[0]);
                            String name = parts[1];
                            String lastName = parts[2];
                            String parcelID = parts[3];

                            // Creating and adding customer to customerQueue
                            Customer customer = new Customer(sequenceNo, name, lastName, parcelID);
                            customerQueue.addCustomer(customer);

                            // Adding customer to customer table
                            customerTableModel.addRow(new Object[]{sequenceNo, name, lastName, parcelID});
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Customers imported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading the file: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Error in file data format: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "File selection cancelled.");
            }
        });


        showLogButton.addActionListener(e -> logTextArea.setText(log.getLog()));

        frame.setVisible(true);
    }


    private String generateNewParcelID() {
        int highestID = 0;
        for (Parcel parcel : parcelMap.getAllParcels().values()) {
            String parcelID = parcel.getParcelID();
            if (parcelID.startsWith("P")) {
                try {
                    int currentID = Integer.parseInt(parcelID.substring(1)); // Extract number after 'P'
                    highestID = Math.max(highestID, currentID); // Get the maximum current ID
                } catch (NumberFormatException e) {
                    // Handle the case where parcelID is not formatted correctly
                }
            }
        }
        highestID++; // Increment the highest ID by 1
        return String.format("P%03d", highestID); // Format to always have three digits (e.g., P001, P002)
    }

    public static void main(String[] args) {
        new ParcelManagementGUI();
    }
}
