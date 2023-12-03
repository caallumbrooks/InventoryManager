package org.example;

import java.io.*;
import java.util.Scanner;

public class TransactionManager {

    public static void addItem(String itemsPath, String transactionsPath, Scanner input) {
        // start of the try block
        try {
            // creation of file object using the file path specified earlier in the program
            File itemsFile = new File(InventoryManager.get_items_path());
            // variables to store the ID of the new item
            int newItemId;
            String newItemIdFormatted;

            // checks if the file is empty or not by checking if it has a non-zero length
            if (itemsFile.exists() && itemsFile.length() > 0) {
                // object created to read contents of the items file
                BufferedReader reader = new BufferedReader(new FileReader(itemsFile));
                // strings that will be used to track the last line read from the file
                String lastLine = null, line;

                // loop to read each line from the file
                // when there are no more lines to read the loop exists
                // the last not null line is stored in the var lastLine
                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }

                // splits the last line into an array of strings using commas
                // extracts the first elements (ID) and increments it by 1 to get the ID for the new item
                // formats the string with leading zeros as required by the brief
                String[] parts = lastLine.split(",");
                newItemId = Integer.parseInt(parts[0]) + 1;
                newItemIdFormatted = String.format("%05d", newItemId);
                // if file is empty or non-existent, set ID to 1 and format with the leading zeros
            } else {
                newItemId = 1;
                newItemIdFormatted = String.format("%05d", newItemId);
            }

            // scanner object to get user input
            // self-explanatory fields for the user to input data for
            // total value is calculated by multiplying price and quantity
            System.out.print("Enter Item Description: ");
            String description = input.nextLine();
            System.out.print("Enter Unit Price in £: ");
            double price = input.nextDouble();
            System.out.print("Enter Current No of items in stock: ");
            int quantity = input.nextInt();
            double totalValue = price * quantity;

            // creates FileWriter to write to the file specified
            // writes a formatted string with the new item info
            FileWriter writer = new FileWriter(InventoryManager.get_items_path(), true);
            writer.write(String.format("%05d,%s,%.2f,%d,%.2f\n", newItemId, description, price, quantity, totalValue));
            // closes writer
            writer.close();

            // print statement to show the item has been added
            System.out.println("New Item Added");

            // method containing information about the transaction for the report
            recordTransaction(newItemIdFormatted, description, quantity, totalValue, "added", -1, input);

            // catch block if an error occurs in the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // end of addItem() method

    public static void updateQuantity(String itemsPath, String transactionsPath, Scanner input) {
        // start of the try block
        try {
            // creates object to read contents of the file specified
            BufferedReader reader = new BufferedReader(new FileReader(InventoryManager.get_items_path()));
            // used to build new version of file with updated quantities
            StringBuilder updatedData = new StringBuilder();

            // prompt to enter item ID in the format 00001
            // stores in variable itemIdToUpdate
            System.out.print("Enter the Item ID to update quantity: ");
            String itemIdToUpdate = input.next();

            // declares variables to:
            // hold each line read from the file,
            // boolean to see if item was found,
            // array of strings to hold parts of each line split by commas
            String line;
            boolean itemFound = false;
            String[] parts = null;

            // loop to read each line from the file and then exit
            while ((line = reader.readLine()) != null) {
                // splits line into an array of strings using commas for different parts of the item
                parts = line.split(",");
                // condition to check if itemID matches the itemIdToUpdate entered by user
                if (parts[0].equals(itemIdToUpdate)) {
                    // if item is found, prompt user to enter new quantity
                    // code then updates the relevant parts of the item info and calculates new total value
                    // joins back to a single string
                    itemFound = true;
                    System.out.print("Enter the new quantity: ");
                    int newQuantity = input.nextInt();
                    parts[3] = String.valueOf(newQuantity);
                    parts[4] = String.valueOf(newQuantity * Double.parseDouble(parts[2]));
                    line = String.join(",", parts);

                    // called to record transaction for daily report
                    recordTransaction(itemIdToUpdate, parts[1], newQuantity, Double.parseDouble(parts[4]), "updated", newQuantity, input);
                }
                // appends modified line, includes a newline character to separate from next line
                updatedData.append(line).append("\n");
            }

            // closes reader
            reader.close();

            // if itemFound flag is false after the loop, the item was not found in the file so error is printed
            if (!itemFound) {
                System.out.println("Item not found.");
                return;
            }

            // writes to the specified file
            // writes updated data to the file and then closes writer
            FileWriter writer = new FileWriter(InventoryManager.get_items_path());
            writer.write(updatedData.toString());
            writer.close();

            // print statement confirming item quantity updated
            System.out.println("Item quantity updated");

            // catch block if an error occurs in the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // end of updateQuantity() method

    public static void removeItem(String itemsPath, String transactionsPath, Scanner input) {
        // start of the try block
        try {
            // creates object to read contents of the file specified
            BufferedReader reader = new BufferedReader(new FileReader(InventoryManager.get_items_path()));
            // used to build new version of file with the item removed
            StringBuilder updatedData = new StringBuilder();

            // prompt for user to enter itemID for the item to be removed, stored in variable itemIdToRemove
            System.out.print("Enter the Item ID to remove: ");
            String itemIdToRemove = input.next();

            // declares variables to:
            // hold each line read from the file,
            // boolean to see if item was found,
            // array of strings to hold parts of each line split by commas
            String line;
            boolean itemFound = false;
            String[] parts = null;

            // loop to read each line from the file and then exit
            while ((line = reader.readLine()) != null) {
                // splits line into an array of strings using commas for different parts of the item
                parts = line.split(",");
                // condition to check if itemID matches the itemIdToRemove entered by user
                if (parts[0].equals(itemIdToRemove)) {
                    // if item found, sets itemFound flag to true
                    itemFound = true;
                    // if item not found, appends line to updatedData, skips the line containing the item to be removed
                } else {
                    updatedData.append(line).append("\n");
                }
            }

            // closes the reader
            reader.close();

            // if itemFound flag is false after the loop, the item was not found in the file so error is printed
            if (!itemFound) {
                System.out.println("Item not found.");
                return;
            }

            // writes to the specified file
            // writes updated data to the file and then closes writer
            FileWriter writer = new FileWriter(InventoryManager.get_items_path());
            writer.write(updatedData.toString());
            writer.close();

            // print statement confirming item has been removed
            System.out.println("Item Removed");

            // called to record transaction for daily report
            recordTransaction(itemIdToRemove, parts[1], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), "removed", -1, input);

            // catch block if an error occurs in the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // end of removeItem() method

    public static void viewReport(String itemsPath, String transactionsPath, Scanner input) {
        // start of the try block
        try {
            // creates object to read contents of the file specified
            BufferedReader reader = new BufferedReader(new FileReader(InventoryManager.get_transactions_path()));
            // declare string variable to hold each line read from the file
            String line;

            // print statements to create table for the report
            System.out.println("DAILY TRANSACTION REPORT");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------");
            System.out.println("| Transaction ID | Item ID   | Quantity | Total Value (£) | Transaction Type | Stock Remaining |");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------");

            // loop to read each line from file, when there are no more lines to read the loop exits
            while ((line = reader.readLine()) != null) {
                // splits into an array of strings using commas, parts represents different attributes of a transaction
                String[] parts = line.split(",");
                // checks array for at least 6 elements to line up with expected number of fields
                if (parts.length >= 6) {
                    // print statement to format transaction information correctly
                    System.out.printf("| %-15s | %-10s | %-9s | %-16s | %-17s | %-15s |\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    System.out.println("-------------------------------------------------------------------------------------------------------------------------");
                    // if line does not have enough parts print error for invalid format
                } else {
                    System.out.println("Invalid format in line: " + line);
                }
            }

            // closes reader
            reader.close();

            // catch block if an error occurs in the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // end of viewReport() method

    public static void recordTransaction(String transactionID, String itemID, int quantity, double totalValue, String transactionType, int stockRemaining, Scanner input) {
        // checks if stockRemaining is set to -1, if set to -1 then stockRemaining parameter was not provided when calling method
        // if stockRemaining = 1, check transactionType to determine how to calculate the value
        // if transactionType is added, sets stockRemaining to quantity
        // if transactionType is removed, set stockRemaining to 0
        // if transactionType is updated, prompt user to enter new quantity and assign that value to stockRemaining
        if (stockRemaining == -1) {
            if (transactionType.equals("added")) {
                stockRemaining = quantity;
            } else if (transactionType.equals("removed")) {
                stockRemaining = 0;
            } else if (transactionType.equals("updated")) {
                System.out.print("Enter the new quantity: ");
                stockRemaining = input.nextInt();
            }
        }

        // try block for file writing
        // creates writer to write to file specified
        // writes a formatted string containing transaction information
        try {
            FileWriter writer = new FileWriter(InventoryManager.get_transactions_path(), true);
            writer.write(String.format("%s,%s,%d,%.2f,%s,%d\n", transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
            // closes the writer
            writer.close();
            // catch block if an error occurs in the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // end of recordTransaction() method

    public static void searchItem(String itemsPath, String transactionsPath, Scanner input) {
        // start of the try block
        try {
            // creates object to read contents of the file specified
            BufferedReader reader = new BufferedReader(new FileReader(InventoryManager.get_items_path()));

            // prompt to enter the ID of the item to be searched for
            // stores in variable itemIdToSearch
            System.out.print("Enter the Item ID to search for: ");
            String itemIdToSearch = input.next();

            // declares variables
            // line variable to hold each line read from the file
            // itemFound variable to track whether item was found using boolean flag
            String line;
            boolean itemFound = false;

            // loop to read each line from file, when there are no more lines to read the loop exits
            while ((line = reader.readLine()) != null) {
                // splits into an array of strings using commas, parts represents different attributes of an item
                String[] parts = line.split(",");
                // condition to check if item ID matches the itemIdToSearch entered by the user
                if (parts[0].equals(itemIdToSearch)) {
                    // print statements for details of item if it is found
                    // sets itemFound to true and breaks the loop if item found
                    itemFound = true;
                    System.out.println("Item Found:");
                    System.out.println("Item ID: " + parts[0]);
                    System.out.println("Item Description: " + parts[1]);
                    System.out.println("Unit Price (£): " + parts[2]);
                    System.out.println("Current No of items in stock: " + parts[3]);
                    System.out.println("Total value of items in stock (£): " + parts[4]);
                    break;
                }
            }

            // closes the reader
            reader.close();

            // if flag is still false after the loop, item was not found
            // print statement indicating item was not found
            if (!itemFound) {
                System.out.println("Item not found.");
            }

            // catch block if an error occurs in the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // end of searchItem() method
}
