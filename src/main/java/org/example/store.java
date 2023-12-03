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

		// Declares variable 'userinput' which is of int type
		// Stores the user's input for selecting menu options
		int userinput;

		// Start of the do-while loop
		// code gets executed at least once
		// the condition inside while statement gets check to see if loop should continue
		do {
			// calls function 'displayMenu' which displays options to user
			displayMenu();
			// uses Scanner class, reads int value entered by the user and stores to 'userinput' variable
			userinput = input.nextInt();
			// Switch statement based on user input, checks value of input against the cases
			switch (userinput) {
				// if user input = 1, calls method 'addItem()'
				// responsible for adding items
				case 1:
					addItem();
					break;
				// if user input = 2, calls method 'updateQuantity()'
				// responsible for updating the quantity of an item
				case 2:
					updateQuantity();
					break;
				// if user input = 3, calls method 'removeItem()'
				// responsible for removing an item
				case 3:
					removeItem();
					break;
				// if user input = 4, calls method 'viewReport()'
				// responsible for displaying the report
				case 4:
					viewReport();
					break;
				// if user input = 5, calls method 'searchItem()'
				// responsible for searching for an item
				case 5:
					searchItem();
					break;
				// if user input = 6, exits the loop
				case 6:
					break;
					// error message if user input does not equal any of the specified values
				default:
					System.out.println("This doesn't appear to be a valid option...!");
					break;
			}
			// end of switch statement

			// line to separate each iteration of the loop
			System.out.println("\n-----------------------------------------------");

			// end of do-while loop, endless loop as long as the user input is not equal to 6
		} while (userinput != 6);

		// thank you for using this program message after loop exists
		System.out.println("\n\n Thanks for using this program...!");
	}

	// declaration of menu method
	// prints out menu options to the console, allowing the user to select the desired operation
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
		// start of the try block
		try {
			// creation of file object using the file path specified earlier in the program
			File itemsFile = new File(items_path);
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
			Scanner input = new Scanner(System.in);
			System.out.print("Enter Item Description: ");
			String description = input.nextLine();
			System.out.print("Enter Unit Price in £: ");
			double price = input.nextDouble();
			System.out.print("Enter Current No of items in stock: ");
			int quantity = input.nextInt();
			double totalValue = price * quantity;

			// creates FileWriter to write to the file specified
			// writes a formatted string with the new item info
			FileWriter writer = new FileWriter(items_path, true);
			writer.write(String.format("%05d,%s,%.2f,%d,%.2f\n", newItemId, description, price, quantity, totalValue));
			// closes writer
			writer.close();

			// print statement to show the item has been added
			System.out.println("New Item Added");

			// method containing information about the transaction for the report
			recordTransaction(newItemIdFormatted, description, quantity, totalValue, "added", -1);

			// catch block if an error occurs in the try block
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// end of addItem() method

	// Section of code that allows the user to update the quantity of an item in the inventory
	public static void updateQuantity() {
		// start of the try block
		try {
			// creates object to read contents of the file specified
			BufferedReader reader = new BufferedReader(new FileReader(items_path));
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
					recordTransaction(itemIdToUpdate, parts[1], newQuantity, Double.parseDouble(parts[4]), "updated", newQuantity);
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
			FileWriter writer = new FileWriter(items_path);
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

	// Section of code that allows user to remove an item from the inventory
	public static void removeItem() {
		// start of the try block
		try {
			// creates object to read contents of the file specified
			BufferedReader reader = new BufferedReader(new FileReader(items_path));
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
			FileWriter writer = new FileWriter(items_path);
			writer.write(updatedData.toString());
			writer.close();

			// print statement confirming item has been removed
			System.out.println("Item Removed");

			// called to record transaction for daily report
			recordTransaction(itemIdToRemove, parts[1], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), "removed", -1);

			// catch block if an error occurs in the try block
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// end of removeItem() method

	// Section of code that allows user to view the daily transaction report
	public static void viewReport() {
		// start of the try block
		try {
			// creates object to read contents of the file specified
			BufferedReader reader = new BufferedReader(new FileReader(transactions_path));
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


	// Section of code to record the transactions for the daily report
	public static void recordTransaction(String transactionID, String itemID, int quantity, double totalValue, String transactionType, int stockRemaining) {
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
			FileWriter writer = new FileWriter(transactions_path, true);
			writer.write(String.format("%s,%s,%d,%.2f,%s,%d\n", transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
			// closes the writer
			writer.close();
			// catch block if an error occurs in the try block
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// end of recordTransaction() method

	// Section of code that allows user to search for an item based on ID (e.g 00001)
	public static void searchItem() {
		// start of the try block
		try {
			// creates object to read contents of the file specified
			BufferedReader reader = new BufferedReader(new FileReader(items_path));

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
