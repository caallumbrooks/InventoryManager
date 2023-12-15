package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class db_GUIManager {
    private final JFrame frame;
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton removeButton;
    private final JButton viewButton;
    private final JButton searchButton;
    private final JTextField itemIDField;
    private final JTextField descriptionField;
    private final JTextField priceField;
    private final JTextField quantityField;
    private final JTextArea resultArea;

    public db_GUIManager(ActionListener actionListener) {
        frame = new JFrame("Inventory Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Quantity");
        removeButton = new JButton("Remove Item");
        viewButton = new JButton("View Report");
        searchButton = new JButton("Search Item");

        itemIDField = new JTextField(10);
        descriptionField = new JTextField(20);
        priceField = new JTextField(10);
        quantityField = new JTextField(10);

        resultArea = new JTextArea();

        addButton.addActionListener(actionListener);
        updateButton.addActionListener(actionListener);
        removeButton.addActionListener(actionListener);
        viewButton.addActionListener(actionListener);
        searchButton.addActionListener(actionListener);

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

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    public void initializeUI() {
        frame.setVisible(true);
    }

    public void handleAction(ActionEvent e, db_ItemManager dbItemManager, db_TransactionManager dbTransactionManager) {
        if (e.getSource() == addButton) {
            dbItemManager.addItem(itemIDField.getText(), descriptionField.getText(),
                    priceField.getText(), quantityField.getText(), resultArea);
        } else if (e.getSource() == updateButton) {
            String itemIdToUpdate = itemIDField.getText();
            String newQuantityStr = quantityField.getText();
            dbItemManager.updateQuantity(itemIdToUpdate, newQuantityStr, resultArea);
        } else if (e.getSource() == removeButton) {
            dbItemManager.removeItem(itemIDField.getText(), resultArea);
        } else if (e.getSource() == viewButton) {
            dbTransactionManager.viewReport();
        } else if (e.getSource() == searchButton) {
            dbItemManager.searchItem(itemIDField.getText(), resultArea);
        }
    }
}

