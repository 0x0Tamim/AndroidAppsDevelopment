package com.ewu.bloodnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BloodNet.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE donors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "blood_group TEXT, " +
                "phone TEXT, " +
                "whatsapp TEXT, " +
                "district TEXT, " +
                "area TEXT, " +
                "last_donation TEXT" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS donors");
        onCreate(db);
    }


    public boolean insertDonor(String name, String bloodGroup, String phone,
                               String whatsapp, String district, String area, String lastDonation) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("blood_group", bloodGroup);
        values.put("phone", phone);
        values.put("whatsapp", whatsapp);
        values.put("district", district);
        values.put("area", area);
        values.put("last_donation", lastDonation);

        long result = db.insert("donors", null, values);

        return result != -1;
    }



}


