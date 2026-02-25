package com.example.antoineboylston_inventorytrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper manages creation and upgrading of the app database.
 * This database stores user login information and inventory items.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventoryApp.db";
    private static final int DATABASE_VERSION = 3;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Inventory Table
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_QUANTITY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";

        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT UNIQUE, " +
                COLUMN_QUANTITY + " INTEGER)";

        db.execSQL(createUsersTable);
        db.execSQL(createInventoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // Insert new user
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Check if username exists
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username=?",
                new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check login credentials
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE username=? AND password=?",
                new String[]{username, password});

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;

    }

    // Insert new inventory item
    public boolean insertItem(String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);

        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }


    // Retrieve inventory items
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
    }

    // Update quantity
    public boolean updateItem(int id, String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);

        int rows = db.update(TABLE_INVENTORY, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        return rows > 0;
    }

    // Delete item
    public boolean deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_INVENTORY, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        return rows > 0;

    }

    // Check if inventory item exists
    public boolean itemExists(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_INVENTORY + " WHERE " +
                        COLUMN_ITEM_NAME + "=?",
                new String[]{itemName});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Increase quantity of existing item
    public void incrementQuantity(String itemName, int additionalQty) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_QUANTITY + " FROM " + TABLE_INVENTORY +
                        " WHERE " + COLUMN_ITEM_NAME + "=?",
                new String[]{itemName});

        if (cursor.moveToFirst()) {
            int currentQty = cursor.getInt(0);
            int newQty = currentQty + additionalQty;

            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQty);

            db.update(TABLE_INVENTORY, values,
                    COLUMN_ITEM_NAME + "=?",
                    new String[]{itemName});
        }

        cursor.close();
    }

    // Decrease quantity by 1
    public void decreaseQuantity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_QUANTITY + " FROM " + TABLE_INVENTORY +
                        " WHERE " + COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            int currentQty = cursor.getInt(0);

            if (currentQty > 0) {
                int newQty = currentQty - 1;

                ContentValues values = new ContentValues();
                values.put(COLUMN_QUANTITY, newQty);

                db.update(TABLE_INVENTORY, values,
                        COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)});
            }
        }

        cursor.close();
    }

    // Try UPDATE first; if no rows updated, INSERT
    public void addOrIncrementItem(String itemName, int qtyToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Try to increment existing row
        db.execSQL(
                "UPDATE " + TABLE_INVENTORY +
                        " SET " + COLUMN_QUANTITY + " = " + COLUMN_QUANTITY + " + ? " +
                        " WHERE " + COLUMN_ITEM_NAME + " = ?",
                new Object[]{qtyToAdd, itemName}
        );

        // If nothing was updated, insert new
        Cursor check = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + TABLE_INVENTORY +
                        " WHERE " + COLUMN_ITEM_NAME + "=?",
                new String[]{itemName}
        );

        boolean exists = check.moveToFirst();
        check.close();

        if (!exists) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_NAME, itemName);
            values.put(COLUMN_QUANTITY, qtyToAdd);
            db.insert(TABLE_INVENTORY, null, values);
        }
    }

    // Get single item by ID
    public Cursor getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_INVENTORY + " WHERE " + COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }


    // Debug method to check what's in database
    public void printAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            android.util.Log.d("DB_DEBUG", "ID: " + id + " Name: " + name + " Qty: " + qty);
        }

        cursor.close();
    }




}

