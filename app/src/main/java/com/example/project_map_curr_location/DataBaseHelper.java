package com.example.project_map_curr_location;


import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS moto (id NUMERIC PRIMARY KEY AUTOINCREMENT, " +
                "user_id NUMERIC, speed INTEGER, latitude VARCHAR, longitude VARCHAR, altitude VARCHAR)");
        // добавление начальных данных
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS moto");
        onCreate(db);
    }
}