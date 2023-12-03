package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class StoreGUI extends JFrame implements ActionListener {

    // Paths to files
    private static final String ITEMS_PATH = "C:\\Users\\callu\\Downloads\\I2P-Assignment-starter-code\\InventoryManager\\src\\main\\java\\org\\example\\items.txt";
    private static final String TRANSACTIONS_PATH = "src/main/java/org/example/transactions.txt";

    // GUI Components
    private JButton addButton, updateButton, removeButton, viewButton, searchButton;
    private JTextField itemIDField, descriptionField, priceField, quantityField;
    private JTextArea resultArea;

    public StoreGUI() {
        try {
            // using nimbus to make GUI look a bit better
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up the GUI frame
        setTitle("Inventory Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize buttons
        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Quantity");
        removeButton = new JButton("Remove Item");
        viewButton = new JButton("View Report");
        searchButton = new JButton("Search Item");

        // Initialize text fields
        itemIDField = new JTextField(10);
        descriptionField = new JTextField(20);
        priceField = new JTextField(10);
        quantityField = new JTextField(10);

        // Initialize result area
        resultArea = new JTextArea();

        // Add action listeners to buttons
        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        removeButton.addActionListener(this);
        viewButton.addActionListener(this);
        searchButton.addActionListener(this);

        // Create panels
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Item ID: "));
        inputPanel.add(itemIDField);
        inputPanel.add(new JLabel("Description: "));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Price: "));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity: "));
        inputPanel.add(quantityField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(removeButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);

        // Add panels to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Display the frame
        setVisible(true);

        // Code to clear the daily transaction report after each execution
        try {
            FileWriter writer = new FileWriter(TRANSACTIONS_PATH);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        // conditional statement to check if addItem() method should be called
        if (e.getSource() == addButton) {
            addItem();
            // conditional statement to check if updateQuantity() method should be called
        } else if (e.getSource() == updateButton) {
            updateQuantity();
            // conditional statement to check if removeItem() method should be called
        } else if (e.getSource() == removeButton) {
            removeItem();
            // conditional statement to check if viewReport() method should be called
        } else if (e.getSource() == viewButton) {
            viewReport();
            // conditional statement to check if searchItem() method should be called
        } else if (e.getSource() == searchButton) {
            searchItem();
        }
    }
    // end of actionPerformed() method

    // Add item to the inventory
    private void addItem() {
        // start of the try block
        try {
            // extracting information from the text fields in the GUI
            // retrieves text entered into description field, assigns to var description
            String description = descriptionField.getText();
            // converts text in price field to a double, assigns to var price
            double price = Double.parseDouble(priceField.getText());
            // converts text in quantity field to an integer, assigns to var quantity
            int quantity = Integer.parseInt(quantityField.getText());
            // totalValue is calculated as a product of price and quantity
            double totalValue = price * quantity;

            // Add the item to the inventory
            // handles file operations
            FileWriter writer = new FileWriter(ITEMS_PATH, true);
            writer.write(String.format("%s,%s,%.2f,%d,%.2f\n", generateItemID(), description, price, quantity, totalValue));
            writer.close();

            // Record the transaction
            recordTransaction(generateItemID(), generateItemID(), quantity, totalValue, "added", quantity);

            // clears the text fields and sets the result area to display new item added message
            descriptionField.setText("");
            priceField.setText("");
            quantityField.setText("");

            resultArea.setText("New Item Added");
            // executes if an exception occurs in the try block
        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Error adding item. Please check your input.");
        }
    }
    // end of addItem() method

    // Generate a unique item ID
    private String generateItemID() {
        // start of the try block
        try {
            // declares variables
            // newItemId stores the new item ID
            // newItemIdFormatted stores formatted string of new item ID
            File itemsFile = new File(ITEMS_PATH);
            int newItemId;
            String newItemIdFormatted;

            // conditional statement checks if file exists and is not empty
            // if true, read contents of the file
            // initialise to null and declare variable line to store each line read from file
            if (itemsFile.exists() && itemsFile.length() > 0) {
                BufferedReader reader = new BufferedReader(new FileReader(itemsFile));
                String lastLine = null, line;

                // loop to read each line from file
                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }

                // executed if file exists and is not empty
                // split last line of file with commas
                // increment item ID by 1 to get new item ID
                // if file doesn't exist or is empty, initialise item ID to 1 and format
                String[] parts = lastLine.split(",");
                newItemId = Integer.parseInt(parts[0]) + 1;
                newItemIdFormatted = String.format("%05d", newItemId);
            } else {
                newItemId = 1;
                newItemIdFormatted = String.format("%05d", newItemId);
            }

            // returns generated and formatted item ID
            return newItemIdFormatted;
            // executes if an exception occurs in the try block
            // return null to indicate issue when generating item ID
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // end of generateItemID() method

    // Update item quantity
    private void updateQuantity() {
        try {
            String itemIdToUpdate = itemIDField.getText();
            int newQuantity = Integer.parseInt(quantityField.getText());

            // Initialize parts to store item details
            String[] parts = null;

            BufferedReader reader = new BufferedReader(new FileReader(ITEMS_PATH));
            StringBuilder updatedData = new StringBuilder();
            String line;
            boolean itemFound = false;

            while ((line = reader.readLine()) != null) {
                parts = line.split(",");
                if (parts[0].equals(itemIdToUpdate)) {
                    itemFound = true;
                    parts[3] = String.valueOf(newQuantity);
                    parts[4] = String.valueOf(newQuantity * Double.parseDouble(parts[2]));
                    line = String.join(",", parts);
                }
                updatedData.append(line).append("\n");
            }

            reader.close();

            if (!itemFound) {
                resultArea.setText("Item not found.");
                return;
            }

            FileWriter writer = new FileWriter(ITEMS_PATH);
            writer.write(updatedData.toString());
            writer.close();

            itemIDField.setText("");
            quantityField.setText("");

            // Record the transaction
            recordTransaction(generateItemID(), itemIdToUpdate, newQuantity, newQuantity * Double.parseDouble(parts[2]), "updated", newQuantity);

            resultArea.setText("Item quantity updated");
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            resultArea.setText("Error updating quantity. Please check your input.");
        }
    }


    // Remove item from inventory
    private void removeItem() {
        try {
            String itemIdToRemove = itemIDField.getText();

            // Remove the item from the inventory
            BufferedReader reader = new BufferedReader(new FileReader(ITEMS_PATH));
            StringBuilder updatedData = new StringBuilder();
            String line;
            boolean itemFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(itemIdToRemove)) {
                    itemFound = true;
                } else {
                    updatedData.append(line).append("\n");
                }
            }

            reader.close();

            if (!itemFound) {
                resultArea.setText("Item not found.");
                return;
            }

            FileWriter writer = new FileWriter(ITEMS_PATH);
            writer.write(updatedData.toString());
            writer.close();

            itemIDField.setText("");

            // Record the transaction
            recordTransaction(generateItemID(), itemIdToRemove, 0, 0, "removed", 0);

            resultArea.setText("Item Removed");
        } catch (IOException e) {
            e.printStackTrace();
            resultArea.setText("Error removing item. Please check your input.");
        }
    }

    // View transaction report
    private void viewReport() {
        try {
            StringBuilder reportText = new StringBuilder();

            reportText.append("DAILY TRANSACTION REPORT\n");
            reportText.append("-----------------------------------------------\n");
            reportText.append(String.format("%-22s%-15s%-17s%-25s%-25s%-20s\n", "Transaction ID", "Item ID", "Quantity", "Total Value (£)", "Transaction Type", "Stock Remaining"));

            BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_PATH));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    reportText.append(String.format("%-28s%-17s%-21s%-30s%-35s%-25s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                } else {
                    reportText.append("Invalid format in line: ").append(line).append("\n");
                }
            }

            reader.close();

            // Create a new frame to display the report
            JFrame reportFrame = new JFrame("Transaction Report");
            JTextArea reportArea = new JTextArea();
            reportArea.setEditable(false);
            reportArea.setText(reportText.toString());

            reportFrame.add(new JScrollPane(reportArea));
            reportFrame.setSize(600, 400);
            reportFrame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordTransaction(String transactionID, String itemID, int quantity, double totalValue, String transactionType, int stockRemaining) {
        if (stockRemaining == -1) {
            // Prompt user for stockRemaining
            String stockRemainingInput = JOptionPane.showInputDialog("Enter stock remaining:");
            stockRemaining = Integer.parseInt(stockRemainingInput);
        }

        try {
            FileWriter writer = new FileWriter(TRANSACTIONS_PATH, true);
            writer.write(String.format("%s,%s,%d,%.2f,%s,%d\n", transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Search for an item, uses Jframe to open new window for the search results
    private void searchItem() {
        try {
            String itemIdToSearch = itemIDField.getText();

            BufferedReader reader = new BufferedReader(new FileReader(ITEMS_PATH));
            String line;
            StringBuilder resultText = new StringBuilder();
            boolean itemFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(itemIdToSearch)) {
                    itemFound = true;
                    resultText.append("Item Found:\n");
                    resultText.append("Item ID: ").append(parts[0]).append("\n");
                    resultText.append("Item Description: ").append(parts[1]).append("\n");
                    resultText.append("Unit Price (£): ").append(parts[2]).append("\n");
                    resultText.append("Current No of items in stock: ").append(parts[3]).append("\n");
                    resultText.append("Total value of items in stock (£): ").append(parts[4]).append("\n");
                    break;
                }
            }

            reader.close();

            if (!itemFound) {
                resultText.append("Item not found.\n");
                resultText.append("Enter a valid Item ID in the previous window.");
            }

            // Create a new frame to display the search results
            JFrame searchResultFrame = new JFrame("Search Results");
            JTextArea searchResultArea = new JTextArea();
            searchResultArea.setEditable(false);
            searchResultArea.setText(resultText.toString());

            searchResultFrame.add(new JScrollPane(searchResultArea));
            searchResultFrame.setSize(600, 400);
            searchResultFrame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StoreGUI());
    }
}
