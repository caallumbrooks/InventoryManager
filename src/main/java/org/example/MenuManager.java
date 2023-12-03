package org.example;

public class MenuManager {
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
}