package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class db_StoreDB extends JFrame implements ActionListener {
    private static final String DATABASE_URI = "mongodb+srv://s257028:27N2BimT0OcdrCHC@inventorydb.0tsq7ii.mongodb.net/test?retryWrites=true&w=majority";
    private static final String DB_NAME = "InventoryDB";
    private static final String COLLECTION_NAME = "items";
    private final db_ItemManager dbItemManager;
    private final db_TransactionManager dbTransactionManager;
    private final db_GUIManager guiManager;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public db_StoreDB() {
        try {
            MongoClient mongoClient = MongoClients.create(DATABASE_URI);
            database = mongoClient.getDatabase(DB_NAME);
            collection = database.getCollection(COLLECTION_NAME);

            // Clear the transactions collection at the beginning of each run
            database.getCollection("transactions").deleteMany(new Document());

            collection.createIndex(Filters.eq("itemID", 1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbItemManager = new db_ItemManager(collection, database);
        dbTransactionManager = new db_TransactionManager(database.getCollection("transactions"));
        guiManager = new db_GUIManager(this);

        guiManager.initializeUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(db_StoreDB::new);
    }

    public void actionPerformed(ActionEvent e) {
        guiManager.handleAction(e, dbItemManager, dbTransactionManager);
    }
}