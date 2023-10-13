package org.example;/* UNIVERSITY OF SUFFOLK - INTRODUCTION TO PROGRAMMING
 * Module assignment
 * 
 * Module Lead: Dr. Kakia Chatsiou
 * Last updated 2022-02-25
 * 
 * The assignment starter code consists of 3 files:
 * 
 * a) org.example.store.java: this file contains starting code for the inventory
 * management control system. See assignment brief document for 
 * more information on how to build the rest of the application.
 * 
 * b) items.txt: this file contains a list of all items in the inventory
 * with information about their quantities and total price in stock. See 
 * assignment text for more information.
 * 
 * c) transactions.txt: this file contains a list of all the transactions
 * for the day. You will be using it to print out the report of transactions
 * Each time a transaction happens i.e. an item is added or removed, 
 * a record should be stored in transactions.txt
 *  
 *
 * You are asked to work on expanding the starter code so that your Java app can do the following:
 * 
 *  - read and output to the 2 files (transactions.txt, items.txt) as appropriate
 *  - autogenerate a (5-digit) item id ie. 00001 for each new item
 *  - add a new item to the inventory (by appending a line to items.txt) 
 *  - update the quantity of an item already in org.example.store (in items.txt)
 *  - remove an item from the inventory (by removing relevant entry in items.txt)
 *  - search for an item in the inventory (items.txt)
 *  - generate and print a daily transaction report (using transactions.txt)
 * 
 * Check out the full assignment brief for more information about the report.
 */


import java.io.*;
import java.util.Date;
import java.util.Scanner;

public class store {
	// Path to the items file
	// does not seem to work without absolute path - need to fix this
	private static final String items_path = "C:\\Users\\callu\\Downloads\\I2P-Assignment-starter-code\\InventoryManager\\src\\main\\java\\org\\example\\items.txt";

	// Path to the transactions file
	private static final String transactions_path = "src/main/java/org/example/transactions.txt";

	// Store the program start time
	private static Date programStartTime;

	// Scanner for user input
	private static Scanner input = new Scanner(System.in);

	// Main method for running the inventory management system
	public static void main(String args[]) {

		// Record the start time
		programStartTime = new Date();


		// Clears the daily transaction report after each execution
		try {
			FileWriter writer = new FileWriter(transactions_path);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
		System.out.println("-----------------------------------------------");

		int userinput;

		do {
			displayMenu();
			userinput = input.nextInt();
			// Switch loop for the options the user can select desired option
			switch (userinput) {
				case 1:
					addItem();
					break;
				case 2:
					updateQuantity();
					break;
				case 3:
					removeItem();
					break;
				case 4:
					viewReport();
					break;
				case 5:
					searchItem();
					break;
				case 6:
					break;
				default:
					System.out.println("This doesn't appear to be a valid option...!");
					break;
			}

			System.out.println("\n-----------------------------------------------");

		} while (userinput != 6);

		System.out.println("\n\n Thanks for using this program...!");
	}

	// Print statements to display a menu for the user with option choices
	public static void displayMenu() {
		System.out.println("1. ADD NEW ITEM");
		System.out.println("2. UPDATE QUANTITY OF EXISTING ITEM");
		System.out.println("3. REMOVE ITEM");
		System.out.println("4. VIEW DAILY TRANSACTION REPORT");
		System.out.println("5. SEARCH FOR AN ITEM IN THE INVENTORY");
		System.out.println("---------------------------------");
		System.out.println("6. Exit");
		System.out.print("\n Enter a choice and Press ENTER to continue[1-6]: ");
	}

	// Section of code that allows the user to add an item to the inventory
	public static void addItem() {
		try {
			File itemsFile = new File(items_path);
			int newItemId;
			String newItemIdFormatted;

			if (itemsFile.exists() && itemsFile.length() > 0) {
				BufferedReader reader = new BufferedReader(new FileReader(itemsFile));
				String lastLine = null, line;

				while ((line = reader.readLine()) != null) {
					lastLine = line;
				}

				String[] parts = lastLine.split(",");
				newItemId = Integer.parseInt(parts[0]) + 1;
				newItemIdFormatted = String.format("%05d", newItemId);
			} else {
				newItemId = 1;
				newItemIdFormatted = String.format("%05d", newItemId);
			}

			Scanner input = new Scanner(System.in);
			System.out.print("Enter Item Description: ");
			String description = input.nextLine();
			System.out.print("Enter Unit Price in £: ");
			double price = input.nextDouble();
			System.out.print("Enter Current No of items in stock: ");
			int quantity = input.nextInt();
			double totalValue = price * quantity;

			FileWriter writer = new FileWriter(items_path, true);
			writer.write(String.format("%05d,%s,%.2f,%d,%.2f\n", newItemId, description, price, quantity, totalValue));
			writer.close();

			System.out.println("New Item Added");

			recordTransaction(newItemIdFormatted, description, quantity, totalValue, "added", -1);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Section of code that allows the user to update the quantity of an item in the inventory
	public static void updateQuantity() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(items_path));
			StringBuilder updatedData = new StringBuilder();

			System.out.print("Enter the Item ID to update quantity: ");
			String itemIdToUpdate = input.next();

			String line;
			boolean itemFound = false;
			String[] parts = null;

			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts[0].equals(itemIdToUpdate)) {
					itemFound = true;
					System.out.print("Enter the new quantity: ");
					int newQuantity = input.nextInt();
					parts[3] = String.valueOf(newQuantity);
					parts[4] = String.valueOf(newQuantity * Double.parseDouble(parts[2]));
					line = String.join(",", parts);
				}
				updatedData.append(line).append("\n");
			}

			reader.close();

			if (!itemFound) {
				System.out.println("Item not found.");
				return;
			}

			FileWriter writer = new FileWriter(items_path);
			writer.write(updatedData.toString());
			writer.close();

			System.out.println("Item quantity updated");

			recordTransaction(itemIdToUpdate, parts[1], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), "updated", Integer.parseInt(parts[3]));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Section of code that allows user to remove an item from the inventory
	public static void removeItem() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(items_path));
			StringBuilder updatedData = new StringBuilder();

			System.out.print("Enter the Item ID to remove: ");
			String itemIdToRemove = input.next();

			String line;
			boolean itemFound = false;
			String[] parts = null;

			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts[0].equals(itemIdToRemove)) {
					itemFound = true;
				} else {
					updatedData.append(line).append("\n");
				}
			}

			reader.close();

			if (!itemFound) {
				System.out.println("Item not found.");
				return;
			}

			FileWriter writer = new FileWriter(items_path);
			writer.write(updatedData.toString());
			writer.close();

			System.out.println("Item Removed");

			recordTransaction(itemIdToRemove, parts[1], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), "removed", -1);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Section of code that allows user to view the daily transaction report
	public static void viewReport() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(transactions_path));
			String line;

			System.out.println("DAILY TRANSACTION REPORT");
			System.out.println("-----------------------------------------------");
			System.out.println("Transaction ID | Item ID | Quantity | Total Value (£) | Transaction Type | Stock Remaining");

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 6) {
					System.out.printf("%-15s%-10s%-12s%-20s%-20s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
				} else {
					System.out.println("Invalid format in line: " + line);
				}
			}

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Section of code to record the transactions for the daily report
	public static void recordTransaction(String transactionID, String itemID, int quantity, double totalValue, String transactionType, int stockRemaining) {
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

		try {
			FileWriter writer = new FileWriter(transactions_path, true);
			writer.write(String.format("%s,%s,%d,%.2f,%s,%d\n", transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Section of code that allows user to search for an item based on ID (e.g 00001)
	public static void searchItem() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(items_path));

			System.out.print("Enter the Item ID to search for: ");
			String itemIdToSearch = input.next();

			String line;
			boolean itemFound = false;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts[0].equals(itemIdToSearch)) {
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

			reader.close();

			if (!itemFound) {
				System.out.println("Item not found.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
