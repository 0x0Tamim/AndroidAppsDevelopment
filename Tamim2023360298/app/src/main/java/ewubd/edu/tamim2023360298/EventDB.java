package ewubd.edu.tamim2023360298;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDB extends SQLiteOpenHelper {

    public EventDB(Context context) {
        super(context, "BirthdayDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE birthdays ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "phone TEXT,"
                + "dob INTEGER"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade called");
    }

    public void insertBirthday(String name, String phone, long dob) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("name", name);
        cols.put("phone", phone);
        cols.put("dob", dob);
        db.insert("birthdays", null, cols);
        db.close();
    }

    public void deleteBirthday(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("birthdays", "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor selectAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT * FROM birthdays ORDER BY dob ASC", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}