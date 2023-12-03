package org.example;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class StoreDB extends JFrame implements ActionListener {

    private static final String DATABASE_URI = "mongodb+srv://s257028:27N2BimT0OcdrCHC@inventorydb.0tsq7ii.mongodb.net/test?retryWrites=true&w=majority";
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private static final String DB_NAME = "InventoryDB";
    private static final String COLLECTION_NAME = "items";

    private JButton addButton, updateButton, removeButton, viewButton, searchButton;
    private JTextField itemIDField, descriptionField, priceField, quantityField;
    private JTextArea resultArea;

    public StoreDB() {
        try {
            // Initialize MongoDB
            mongoClient = MongoClients.create(DATABASE_URI);
            database = mongoClient.getDatabase("InventoryDB");
            collection = database.getCollection("items");

            // Set up indexes (optional)
            collection.createIndex(Filters.eq("itemID", 1));
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
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addItem();
        } else if (e.getSource() == updateButton) {
            updateQuantity();
        } else if (e.getSource() == removeButton) {
            removeItem();
        } else if (e.getSource() == viewButton) {
            viewReport();
        } else if (e.getSource() == searchButton) {
            searchItem();
        }
    }

    // Add item to the inventory
    private void addItem() {
        try {
            String itemId = itemIDField.getText();
            String description = descriptionField.getText();
            String price = String.format("%.1f", Double.parseDouble(priceField.getText())); // Format price to one decimal place
            String quantity = quantityField.getText();
            String totalPrice = String.valueOf(Double.parseDouble(price) * Integer.parseInt(quantity));

            Document document = new Document("id", itemId)
                    .append("description", description)
                    .append("unitPrice", price)
                    .append("qtyInStock", quantity)
                    .append("totalPrice", totalPrice);

            collection.insertOne(document);

            recordTransaction(generateTransactionID(), itemId, Integer.parseInt(quantity), Double.parseDouble(totalPrice), "added", Integer.parseInt(quantity));

            descriptionField.setText("");
            priceField.setText("");
            quantityField.setText("");
            itemIDField.setText("");

            resultArea.setText("New Item Added");
        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Error adding item. Please check your input.");
        }
    }

    // Update item quantity
    private void updateQuantity() {
        try {
            String itemIdToUpdate = itemIDField.getText();
            String newQuantityStr = quantityField.getText(); // Get quantity as string

            Document query = new Document("id", itemIdToUpdate);
            FindIterable<Document> result = collection.find(query);

            boolean itemFound = false;
            Document doc = null;

            for (Document document : result) {
                itemFound = true;
                doc = document;
                doc.put("qtyInStock", newQuantityStr); // Store quantity as string
                double unitPrice = Double.parseDouble(doc.getString("unitPrice")); // Parse unitPrice
                doc.put("totalPrice", String.valueOf(Double.parseDouble(newQuantityStr) * unitPrice)); // Calculate total price and store as string
                collection.replaceOne(Filters.eq("id", itemIdToUpdate), doc);
            }

            if (!itemFound) {
                resultArea.setText("Item not found.");
                return;
            }

            itemIDField.setText("");
            quantityField.setText("");

            // Record the transaction
            recordTransaction(generateTransactionID(), itemIdToUpdate, Integer.parseInt(newQuantityStr), Double.parseDouble(newQuantityStr) * Double.parseDouble(doc.getString("unitPrice")), "updated", Integer.parseInt(newQuantityStr));

            resultArea.setText("Item quantity updated");
        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Error updating quantity. Please check your input.");
        }
    }




    private String generateTransactionID() {
        // Generate a unique transaction ID
        return String.valueOf(System.currentTimeMillis());
    }


    // Remove item from inventory
    private void removeItem() {
        try {
            String itemIdToRemove = itemIDField.getText();

            // Find and delete the document with the given item ID
            Document query = new Document("itemID", itemIdToRemove);
            DeleteResult result = collection.deleteOne(query);

            if (result.getDeletedCount() > 0) {
                itemIDField.setText("");

                // Record the transaction
                recordTransaction(generateTransactionID(), itemIdToRemove, 0, 0, "removed", 0);

                resultArea.setText("Item Removed");
            } else {
                resultArea.setText("Item not found.");
            }

        } catch (Exception e) {
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
            reportText.append(String.format("%-30s%-15s%-17s%-25s%-25s%-20s\n", "Transaction ID", "Item ID", "Quantity", "Total Value (£)", "Transaction Type", "Stock Remaining"));

            MongoCollection<Document> transactionsCollection = database.getCollection("transactions");
            FindIterable<Document> transactions = transactionsCollection.find();

            for (Document transaction : transactions) {
                String transactionID = transaction.getString("transactionID");
                String itemID = transaction.getString("itemID");
                String quantity = transaction.getString("quantity");
                String totalValue = transaction.getString("totalValue");
                String transactionType = transaction.getString("transactionType");
                String stockRemaining = transaction.getString("stockRemaining");

                reportText.append(String.format("%-28s%-17s%-21s%-30s%-35s%-25s\n", transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
            }

            // Create a new frame to display the report
            JFrame reportFrame = new JFrame("Transaction Report");
            JTextArea reportArea = new JTextArea();
            reportArea.setEditable(false);
            reportArea.setText(reportText.toString());

            reportFrame.add(new JScrollPane(reportArea));
            reportFrame.setSize(800, 400);
            reportFrame.setVisible(true);

        } catch (Exception e) {
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
            MongoCollection<Document> transactionsCollection = database.getCollection("transactions");

            String quantityStr = String.valueOf(quantity);
            String totalValueStr = String.valueOf(totalValue);

            Document transactionDoc = new Document("transactionID", transactionID)
                    .append("itemID", itemID)
                    .append("quantity", quantityStr)
                    .append("totalValue", totalValueStr)
                    .append("stockRemaining", String.valueOf(stockRemaining))
                    .append("transactionType", transactionType);

            transactionsCollection.insertOne(transactionDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // Search for an item, uses Jframe to open new window for the search results
    private void searchItem() {
        try {
            String itemIdToSearch = itemIDField.getText();

            Document query = new Document("id", itemIdToSearch); // Use "id" e.g 00001
            FindIterable<Document> result = collection.find(query);

            StringBuilder resultText = new StringBuilder();
            boolean itemFound = false;

            for (Document doc : result) {
                itemFound = true;
                resultText.append("Item Found:\n");
                resultText.append("Item ID: ").append(doc.getString("id")).append("\n"); // Use "id" instead of "itemID"
                resultText.append("Item Description: ").append(doc.getString("description")).append("\n");
                resultText.append("Unit Price (£): ").append(doc.getString("unitPrice")).append("\n"); // Stored as string
                resultText.append("Current No of items in stock: ").append(doc.getString("qtyInStock")).append("\n"); // Stored as string
                resultText.append("Total value of items in stock (£): ").append(doc.getString("totalPrice")).append("\n"); // Stored as string
            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StoreDB());
    }
}
