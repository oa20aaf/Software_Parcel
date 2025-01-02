import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        JButton generateReportButton = new JButton("Generate Report");



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
        buttonPanel.add(generateReportButton);



// Table models and components
        DefaultTableModel customerTableModel = new DefaultTableModel(new String[]{"Sequence No", "First Name","Last Name", "Parcel ID"}, 0);
        JTable customerTable = new JTable(customerTableModel);

        DefaultTableModel parcelTableModel = new DefaultTableModel(new String[]{
                "Parcel ID", "Weight", "Dimensions", "Fee", "Status", "Received Day", "Days Waiting"
        }, 0);
        JTable parcelTable = new JTable(parcelTableModel);

// Add tables to the table panel
        tablePanel.add(new JScrollPane(customerTable));
        tablePanel.add(new JScrollPane(parcelTable));

// Set the preferred size of the input panel (make it smaller in height)
        inputPanel.setPreferredSize(new Dimension(400, 150)); // Adjust the height here

// Allow table panel to expand and take more space
        tablePanel.setPreferredSize(new Dimension(800, 600)); // Adjust width and height as needed

// Add panels to the main panel
        log.addLogEntry("Application Started");
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

// Add the main panel to the frame
        frame.add(mainPanel);


        addCustomerButton.addActionListener(e -> {
            log.addLogEntry("addCustomerButton Invoked");
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
                        log.addLogEntry("The provided Parcel ID does not exist.");
                    }
                } else {
                    // Fields are empty
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields for the customer.");
                    log.addLogEntry("Please fill in all fields for the customer.");
                }
                logTextArea.setText(log.getLog()); // Populate with logs
            });
        });


        generateReportButton.addActionListener(e -> {
            // Initialize counters and lists to store details
            int parcelsCollected = 0;
            int parcelsWaiting = 0;
            double totalEarnings = 0.0;
            int parcelsOver10Days = 0;

            StringBuilder collectedDetails = new StringBuilder();
            StringBuilder waitingDetails = new StringBuilder();
            StringBuilder over10DaysDetails = new StringBuilder();

            // Access the parcelTableModel directly (assuming it's declared as a class member)
            DefaultTableModel model = parcelTableModel; // Access the model directly

            // Check if the table is empty
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No parcels to generate report.");
                return;  // Exit the method early
            }

            // Iterate through the rows of the parcel table model
            for (int i = 0; i < model.getRowCount(); i++) {
                // Get parcel details from each row
                String parcelID = (String) model.getValueAt(i, 0);
                String status = (String) model.getValueAt(i, 4);
                double feeString = (double) model.getValueAt(i, 3);
                long daysWaiting = (long) model.getValueAt(i, 6);
                String dimensions = (String) model.getValueAt(i, 2);
                String dateReceived = (String) model.getValueAt(i, 5);

                // Count how many parcels are collected and add to total earnings
                if ("Collected".equals(status)) {
                    parcelsCollected++;
                    totalEarnings += feeString;  // Add earnings for processed parcels
                    collectedDetails.append(String.format("Parcel ID: %s, Fee: $%.2f, Dimensions: %s, Date Received: %s\n",
                            parcelID, feeString, dimensions, dateReceived));
                } else {
                    // Count parcels still waiting
                    parcelsWaiting++;
                    waitingDetails.append(String.format("Parcel ID: %s, Fee: $%.2f, Dimensions: %s, Date Received: %s\n",
                            parcelID, feeString, dimensions, dateReceived));
                }

                // Count parcels that have been in the depot for more than 10 days
                if (daysWaiting > 10) {
                    parcelsOver10Days++;
                    over10DaysDetails.append(String.format("Parcel ID: %s, Days Waiting: %d, Fee: $%.2f, Dimensions: %s, Date Received: %s\n",
                            parcelID, daysWaiting, feeString, dimensions, dateReceived));
                }
            }

            // Generate the report in a txt file
            try (FileWriter writer = new FileWriter("Parcel_Report.txt")) {
                writer.write("Parcel Report\n");
                writer.write("========================\n");
                writer.write("Parcels Collected: " + parcelsCollected + "\n");
                writer.write("Details of Collected Parcels:\n");
                writer.write(collectedDetails.toString());

                writer.write("\nParcels Still Waiting: " + parcelsWaiting + "\n");
                writer.write("Details of Waiting Parcels:\n");
                writer.write(waitingDetails.toString());

                writer.write("\nTotal Earnings: $" + totalEarnings + "\n");

                writer.write("\nParcels Waiting More Than 10 Days: " + parcelsOver10Days + "\n");
                writer.write("Details of Parcels Waiting More Than 10 Days:\n");
                writer.write(over10DaysDetails.toString());

                writer.write("========================\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Show success message
            JOptionPane.showMessageDialog(null, "Report generated successfully!");
        });


        showLogButton.addActionListener(e -> {
            log.addLogEntry("showLogButton Invoked");
            // Create a new JFrame for the log
            JFrame logFrame = new JFrame("Logs");
            logFrame.setSize(600, 400);

            // JTextArea to display logs
            logTextArea.setEditable(false);
            logTextArea.setText(log.getLog()); // Populate with logs

            // Add the JTextArea to a JScrollPane
            JScrollPane scrollPane = new JScrollPane(logTextArea);

            // Add the scrollPane to the JFrame
            logFrame.add(scrollPane);

            // Make the log frame visible
            logFrame.setVisible(true);
        });

        addParcelButton.addActionListener(e -> {
            log.addLogEntry("addParcelButton Invoked");
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

                try {
                    // Validate all inputs
                    if (weightText.isEmpty() || dimensions.isEmpty() || dateReceived.isEmpty()) {
                        throw new IllegalArgumentException("All fields are required.");
                    }

                    // Weight validation
                    double weight = Double.parseDouble(weightText);
                    if (weight <= 0) {
                        throw new IllegalArgumentException("Weight must be a positive number.");
                    }

                    // Dimensions validation
                    if (!dimensions.matches("\\d+X\\d+X\\d+")) {
                        throw new IllegalArgumentException("Dimensions must be in the format aXbXc with positive integers.");
                    }
                    String[] dims = dimensions.split("X");
                    for (String dim : dims) {
                        int value = Integer.parseInt(dim);
                        if (value <= 0) {
                            throw new IllegalArgumentException("All dimensions must be positive integers.");
                        }
                    }

                    // Date validation
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    dateFormat.setLenient(false);
                    Date date = dateFormat.parse(dateReceived);
                    if (date.after(new Date())) {
                        throw new IllegalArgumentException("Date cannot be in the future.");
                    }

                    // All validations passed, proceed to create parcel
                    String newParcelID = generateNewParcelID();

                    // Calculate the difference in days
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate receivedDate = LocalDate.parse(dateReceived, formatter);
                    LocalDate currentDate = LocalDate.now();
                    long daysDifference = ChronoUnit.DAYS.between(receivedDate, currentDate);


                    Parcel parcel = new Parcel(newParcelID, weight, dimensions, dateReceived);
                    parcelMap.addParcel(parcel);
                    parcelTableModel.addRow(new Object[]{
                            newParcelID, weight, dimensions, parcel.calculateCollectionFee(parcel.getDateReceived()), parcel.getStatus(), dateReceived,daysDifference
                    });
                    JOptionPane.showMessageDialog(frame, "Parcel added successfully.");
                    log.addLogEntry("Added parcel: " + newParcelID);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                    log.addLogEntry(ex.getMessage());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Use DD/MM/YYYY.");
                    log.addLogEntry("Invalid date format. Use DD/MM/YYYY.");
                }
                logTextArea.setText(log.getLog()); // Update logs
            });
        });



        removeCustomerButton.addActionListener(e -> {
            log.addLogEntry("removeCustomerButton Invoked");
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

                System.out.println(customerToRemove.getName());
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
                    log.addLogEntry("Customer with Parcel ID " + parcelIDToRemove + " removed successfully.");
                } else {
                    // Show error message if the customer is not found
                    JOptionPane.showMessageDialog(frame, "Customer with Parcel ID " + parcelIDToRemove + " not found.");
                    log.addLogEntry("Customer with Parcel ID " + parcelIDToRemove + " not found.");
                }
            } else {
                // Show error message if no Parcel ID was entered
                JOptionPane.showMessageDialog(frame, "Please enter a valid Parcel ID.");
                log.addLogEntry("Please enter a valid Parcel ID.");
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });



        // Add ActionListener to removeParcelButton (assuming you have a removeParcelButton in your UI)
        removeParcelButton.addActionListener(e -> {
            log.addLogEntry("removeParcelButton Invoked");
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
                    log.addLogEntry("Parcel " + parcelIDToRemove + " removed successfully.");
                } else {
                    // Show error message if the parcel is not found
                    JOptionPane.showMessageDialog(frame, "Parcel with ID " + parcelIDToRemove + " not found.");
                    log.addLogEntry("Parcel with ID " + parcelIDToRemove + " not found.");
                }
            } else {
                // Show error message if no Parcel ID was entered
                JOptionPane.showMessageDialog(frame, "Please enter a valid Parcel ID.");
                log.addLogEntry("Please enter a valid Parcel ID.");
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });

        processCustomerButton.addActionListener(e -> {
            log.addLogEntry("processCustomerButton Invoked");
            if (!customerQueue.getQueue().isEmpty()) {
                Customer customer = customerQueue.getQueue().peek();
                worker.processCustomer(customer, parcelMap, customerQueue);
                customerTableModel.removeRow(0);
                for (int i = 0; i < parcelTableModel.getRowCount(); i++) {
                    if (parcelTableModel.getValueAt(i, 0).equals(customer.getParcelID())) {
                        parcelTableModel.setValueAt("Collected", i, 4);
                        log.addLogEntry("Processed customer " + customer.getName() + " With Parcel "+customer.getParcelID() + " Fee: $"+parcelTableModel.getValueAt(i,3)+" on "+LocalDate.now());
                        JOptionPane.showMessageDialog(frame, "Processed customer: " + customer.getName() + " With Parcel "+customer.getParcelID() );
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No customers to process.");
                log.addLogEntry("No customers to process.");
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });

        importParcels.addActionListener(e -> {
            log.addLogEntry("importParcels Invoked");
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                log.addLogEntry("file " + file.getName() +" Choosen ");

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
                            log.addLogEntry("Added Parcel "+ parcel.getParcelID() +"  into parcelMap and Parcel Table");

                            // Add to table model
                            if (parcelTableModel != null) {
                                parcelTableModel.addRow(new Object[]{
                                        parcelID, weight, dimensions, collectionFee, status, dateReceived, daysDifference
                                });
                            } else {
                                JOptionPane.showMessageDialog(frame, "Parcel Table Model is not initialized.");
                                log.addLogEntry("Parcel Table Model is not initialized.");
                            }
                        }
                    }

                    JOptionPane.showMessageDialog(frame, "Data imported successfully.");
                    log.addLogEntry("Data imported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading the file: " + ex.getMessage());
                    log.addLogEntry("Error reading the file: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Error in file data format: " + ex.getMessage());
                    log.addLogEntry("Error in file data format: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "File selection cancelled.");
                log.addLogEntry("File selection Cancelled");
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });


        // Action Listener for Export Button
        exportParcels.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                log.addLogEntry("file " + selectedFile.getName() +" is Choosen for exporting parcels data");
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
                    log.addLogEntry("Parcels exported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error exporting parcels: " + ex.getMessage());
                    log.addLogEntry("Error exporting parcels: " + ex.getMessage());
                }
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });


        exportCustomerButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                log.addLogEntry("file " + file.getName() +" is Choosen for exporting customers data");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (Customer customer : customerQueue.getQueue()) {
                        // Writing customer details to the CSV file
                        writer.write(customer.getSequenceNo() + "," + customer.getName() + "," + customer.getLastname() + "," + customer.getParcelID());
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(frame, "Customers exported successfully.");
                    log.addLogEntry("Customers exported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error exporting customers: " + ex.getMessage());
                    log.addLogEntry("Error exporting customers: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Export cancelled.");
                log.addLogEntry("Export cancelled.");
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });

        importCustomerButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                log.addLogEntry("file " + file.getName() +" is Choosen for importing Customer data");

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
                            log.addLogEntry("Added Customer "+ customer.getName() + " " +customer.getLastname() +" into customerQueue and Customer Table");

                            // Adding customer to customer table
                            customerTableModel.addRow(new Object[]{sequenceNo, name, lastName, parcelID});
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Customers imported successfully.");
                    log.addLogEntry("Customers imported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading the file: " + ex.getMessage());
                    log.addLogEntry("Error reading the file: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Error in file data format: " + ex.getMessage());
                    log.addLogEntry("Error in file data format: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "File selection cancelled.");
                log.addLogEntry("File selection cancelled.");
            }
            logTextArea.setText(log.getLog()); // Populate with logs
        });

        logTextArea.setText(log.getLog()); // Populate with logs
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


    public void generateReport() {

    }

    public static void main(String[] args) {
        new ParcelManagementGUI();
    }
}
