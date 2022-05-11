package com.example.project_map_curr_location;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.project_map_curr_location.domain.Moto1;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "app.db", null, 1);
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS moto");
        String createTableMotoStatement = "CREATE TABLE moto (id NUMERIC, " +
                "user_id NUMERIC, speed INTEGER, latitude VARCHAR, longitude VARCHAR, altitude VARCHAR)";
        db.execSQL(createTableMotoStatement);

//        db.execSQL("CREATE TABLE IF NOT EXISTS moto (id NUMERIC PRIMARY KEY AUTOINCREMENT, " +
//                "user_id NUMERIC, speed INTEGER, latitude VARCHAR, longitude VARCHAR, altitude VARCHAR)");
        // добавление начальных данных
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS moto");
        onCreate(db);
    }

    public boolean addOne(Moto1 moto) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id", moto.getId());
        cv.put("user_id", moto.getUser().getId());
        cv.put("speed", moto.getSpeed());
        cv.put("latitude", moto.getLatitude());
        cv.put("longitude", moto.getLongitude());
        cv.put("altitude", moto.getAltitude());

        long insert = db.insert("moto", null, cv);

        return insert != -1;
    }

    public List<Moto1> getAllMoto() {

        List<Moto1> list = new ArrayList<>();

        String query = "SELECT * FROM moto";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do{
                long id = cursor.getLong(0);
                //long user_id = cursor.getLong(1);
                int speed = cursor.getInt(2);
                double latitude = cursor.getDouble(3);
                double longitude = cursor.getDouble(4);
                double altitude = cursor.getDouble(5);

                Moto1 moto = new Moto1(id, speed, latitude, longitude, altitude);
                list.add(moto);
                Log.d("DB_ADD_MOTO", "true");
            } while (cursor.moveToNext());

        } else {
            Log.d("DB_ADD_MOTO", "false");
        }

        cursor.close();
        db.close();
        return list;
    }
}