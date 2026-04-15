package com.example.codefestpractice.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.example.codefestpractice.models.User;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "FlexPOS.db";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = String.format(
                """
                          CREATE TABLE IF NOT EXISTS %s (
                            ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            FIRSTNAME TEXT NOT NULL,
                            MIDDLENAME TEXT NULL,
                            LASTNAME TEXT NOT NULL,
                            EMAIL TEXT UNIQUE NOT NULL,
                            PASSWORD TEXT NOT NULL,
                            ROLE TEXT NOT NULL CHECK (ROLE IN('customer', 'cashier')),
                            CREATED_AT TEXT DEFAULT (datetime('now', 'localtime'))
                        )
                        """, TABLE_USERS);


        String CREATE_TABLE_PRODUCTS = String.format(
                """
                        CREATE TABLE IF NOT EXISTS %s (
                            ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            NAME TEXT NOT NULL,
                            PRICE REAL NOT NULL,
                            IMAGE_RES INTEGER NULL
                        )
                        """, TABLE_PRODUCTS);


        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append(0);
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return password;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL =?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public User authenticateAndGetUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL = ? AND PASSWORD = ?", new String[]{email, hashedPassword});

        User loggedInUser = null;

        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("FIRSTNAME"));
            String middleName = cursor.getString(cursor.getColumnIndexOrThrow("MIDDLENAME"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("LASTNAME"));
            String dbEmail = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"));
            String dbPassword = cursor.getString(cursor.getColumnIndexOrThrow("PASSWORD"));
            String role = cursor.getString(cursor.getColumnIndexOrThrow("ROLE"));

            loggedInUser = new User(dbEmail, dbPassword, role, firstName, middleName, lastName);

            loggedInUser.setRole(role);
        }

        cursor.close();
        return loggedInUser;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("FIRSTNAME", user.getFirstName());
        contentValues.put("MIDDLENAME", user.getMiddleName());
        contentValues.put("LASTNAME", user.getLastName());

        contentValues.put("EMAIL", user.getEmail());
        String hashedPassword = hashPassword(user.getPassword());
        contentValues.put("PASSWORD", hashedPassword);

        contentValues.put("ROLE", user.getRole());

        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }
}