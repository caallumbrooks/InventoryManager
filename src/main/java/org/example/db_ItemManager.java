package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import javax.swing.*;

class db_ItemManager {
    private final MongoCollection<Document> collection;
    private final MongoDatabase database;

    public db_ItemManager(MongoCollection<Document> collection, MongoDatabase database) {
        this.collection = collection;
        this.database = database;
    }

    public void addItem(String itemId, String description, String price, String quantity, JTextArea resultArea) {
        try {
            double parsedPrice = Double.parseDouble(price);
            int parsedQuantity = Integer.parseInt(quantity);
            double totalPrice = parsedPrice * parsedQuantity;

            Document document = new Document("id", itemId)
                    .append("description", description)
                    .append("unitPrice", String.valueOf(parsedPrice))
                    .append("qtyInStock", String.valueOf(parsedQuantity))
                    .append("totalPrice", String.valueOf(totalPrice));

            collection.insertOne(document);

            recordTransaction(generateTransactionID(), itemId, parsedQuantity, totalPrice, "added", parsedQuantity);
            resultArea.setText("New Item Added");
        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Error adding item. Please check your input.");
        }
    }

    public void updateQuantity(String itemIdToUpdate, String newQuantityStr, JTextArea resultArea) {
        try {
            int newQuantity = Integer.parseInt(newQuantityStr);

            Document query = new Document("id", itemIdToUpdate);
            Document doc = collection.find(query).first();

            if (doc != null) {
                double unitPrice = Double.parseDouble(doc.getString("unitPrice"));
                double totalValue = newQuantity * unitPrice;

                doc.put("qtyInStock", String.valueOf(newQuantity));
                doc.put("totalPrice", String.valueOf(totalValue));

                collection.replaceOne(Filters.eq("id", itemIdToUpdate), doc);

                recordTransaction(generateTransactionID(), itemIdToUpdate, newQuantity, totalValue, "updated", newQuantity);

                // Update GUI to indicate success
                resultArea.setText("Item quantity updated");
            }
            // Update GUI to indicate success
            resultArea.setText("Item quantity updated");
        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Item not found. Error updating quantity. Please check your input.");
        }
    }


    public void removeItem(String itemIdToRemove, JTextArea resultArea) {
        try {
            Document query = new Document("id", itemIdToRemove);
            DeleteResult result = collection.deleteOne(query);

            if (result.getDeletedCount() > 0) {
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

    public void searchItem(String itemIdToSearch, JTextArea resultArea) {
        try {
            Document query = new Document("id", itemIdToSearch);
            Document resultDoc = collection.find(query).first();

            if (resultDoc != null) {
                String resultText = "Item Found:\n" +
                        "Item ID: " + resultDoc.getString("id") + "\n" +
                        "Item Description: " + resultDoc.getString("description") + "\n" +
                        "Unit Price (£): " + resultDoc.getString("unitPrice") + "\n" +
                        "Current No of items in stock: " + resultDoc.getString("qtyInStock") + "\n" +
                        "Total value of items in stock (£): " + resultDoc.getString("totalPrice") + "\n";

                displaySearchResult(resultText);
            } else {
                resultArea.setText("Item not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateTransactionID() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void recordTransaction(String transactionID, String itemID, int quantity, double totalValue, String transactionType, int stockRemaining) {
        try {
            MongoCollection<Document> transactionsCollection = database.getCollection("transactions");

            Document transactionDoc = new Document("transactionID", transactionID)
                    .append("itemID", itemID)
                    .append("quantity", quantity)
                    .append("totalValue", totalValue)
                    .append("stockRemaining", stockRemaining)
                    .append("transactionType", transactionType);

            transactionsCollection.insertOne(transactionDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displaySearchResult(String resultText) {
        JFrame searchResultFrame = new JFrame("Search Results");
        JTextArea searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setText(resultText);

        searchResultFrame.add(new JScrollPane(searchResultArea));
        searchResultFrame.setSize(600, 400);
        searchResultFrame.setVisible(true);
    }
}
