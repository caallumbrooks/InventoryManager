package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;

class db_TransactionManager {
    private final MongoCollection<Document> transactionsCollection;

    public db_TransactionManager(MongoCollection<Document> transactionsCollection) {
        this.transactionsCollection = transactionsCollection;
    }

    public void viewReport() {
        try {
            StringBuilder reportText = new StringBuilder();

            reportText.append("DAILY TRANSACTION REPORT\n");
            reportText.append("-----------------------------------------------\n");
            reportText.append(String.format("%-30s%-15s%-17s%-25s%-25s%-20s\n", "Transaction ID", "Item ID", "Quantity", "Total Value (£)", "Transaction Type", "Stock Remaining"));

            // Re-query the transactions collection each time the report is generated
            transactionsCollection.find().forEach((Document transaction) -> {
                String transactionID = transaction.getString("transactionID");
                String itemID = transaction.getString("itemID");
                int quantity = transaction.getInteger("quantity");
                double totalValue = transaction.getDouble("totalValue");
                String transactionType = transaction.getString("transactionType");
                int stockRemaining = transaction.getInteger("stockRemaining");

                reportText.append(String.format("%-28s%-17s%-21s%-30s%-35s%-25s\n",
                        transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
            });

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
}
