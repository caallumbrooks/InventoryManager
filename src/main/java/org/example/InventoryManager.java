package org.example;
import java.util.Date;
import java.util.Scanner;

public class InventoryManager {
    private static final String items_path = "C:\\Users\\callu\\Downloads\\I2P-Assignment-starter-code\\InventoryManager\\src\\main\\java\\org\\example\\items.txt";
    private static final String transactions_path = "src/main/java/org/example/transactions.txt";

    public static String get_items_path() {
        return items_path;
    }

    public static String get_transactions_path() {
        return transactions_path;
    }

    private static Date programStartTime;
    private static Scanner input = ConsoleInput.getScanner(); // Use ConsoleInput.getScanner() to get the Scanner instance

    public static void main(String args[]) {
        programStartTime = new Date();
        try {
            // Clear the daily transaction report after each execution
            FileManager.clearTransactionsFile(transactions_path);

            System.out.println("I N V E N T O R Y    M A N A G E M E N T    S Y S T E M");
            System.out.println("-----------------------------------------------");

            int userinput;

            do {
                MenuManager.displayMenu();
                userinput = input.nextInt();

                switch (userinput) {
                    case 1:
                        TransactionManager.addItem(items_path, transactions_path, input);
                        break;
                    case 2:
                        TransactionManager.updateQuantity(items_path, transactions_path, input);
                        break;
                    case 3:
                        TransactionManager.removeItem(items_path, transactions_path, input);
                        break;
                    case 4:
                        TransactionManager.viewReport(items_path, transactions_path, input);
                        break;
                    case 5:
                        TransactionManager.searchItem(items_path, transactions_path, input);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

