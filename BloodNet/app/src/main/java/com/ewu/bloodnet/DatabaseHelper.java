package com.ewu.bloodnet;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// ei class ta database er kaj kore - SQLite
public class DatabaseHelper extends SQLiteOpenHelper {

    // database er nam ar version
    String dbName = "BloodNet.db";
    int dbVersion = 1;

    // constructor - context lagbe database banate
    public DatabaseHelper(Context context) {
        super(context, "BloodNet.db", null, 1);
    }

    // prothombar app open hoile ei function call hobe, table banabe
    @Override
    public void onCreate(SQLiteDatabase db) {

        // donors table create
        String query = "CREATE TABLE donors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "blood_group TEXT, " +
                "phone TEXT, " +
                "whatsapp TEXT, " +
                "district TEXT, " +
                "area TEXT, " +
                "last_donation TEXT" +
                ")";

        // query execute korchi
        db.execSQL(query);
    }

    // database er version change hole ei function call hobe
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // purano table delete kore notun ta banabo
        db.execSQL("DROP TABLE IF EXISTS donors");
        onCreate(db);
    }

    // ei function donor er data save korbe database e
    public boolean insertDonor(String name, String bloodGroup, String phone,
                               String whatsapp, String district, String area, String lastDonation) {

        // writable database open korchi
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues e data rakhi
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("blood_group", bloodGroup);
        values.put("phone", phone);
        values.put("whatsapp", whatsapp);
        values.put("district", district);
        values.put("area", area);
        values.put("last_donation", lastDonation);

        // insert korchi, result hobe row id, -1 hoile error
        long result = db.insert("donors", null, values);

        // jodi -1 na hoy tahole success, true return korbo
        if (result != -1) {
            return true;
        } else {
            return false;
        }
    }
}
