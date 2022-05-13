package com.example.project_map_curr_location.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.domain.User;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app1.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    private Context context;


    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "app1.db", null, 1);
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DELETE FROM moto");
        String createTableMotoStatement = "CREATE TABLE IF NOT EXISTS moto (id NUMERIC, " +
                "user_id NUMERIC, speed INTEGER, latitude VARCHAR, longitude VARCHAR, altitude VARCHAR)";
        String createTableUserStatement = "CREATE TABLE IF NOT EXISTS user (id NUMERIC, " +
                "name VARCHAR, nickname VARCHAR, email VARCHAR, status VARCHAR)";

        db.execSQL(createTableMotoStatement);
        db.execSQL(createTableUserStatement);

//        db.execSQL("CREATE TABLE IF NOT EXISTS moto (id NUMERIC PRIMARY KEY AUTOINCREMENT, " +
//                "user_id NUMERIC, speed INTEGER, latitude VARCHAR, longitude VARCHAR, altitude VARCHAR)");
        // добавление начальных данных
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS moto");
        db.execSQL("DROP TABLE IF EXISTS user");
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

        db.close();
        return insert != -1;
    }

    public List<Moto1> getAllMoto() {

        //dropTableMoto();
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

                Moto1 moto = new Moto1(id, null, speed, latitude, longitude, altitude);
                list.add(moto);
                Log.d("DB_ADD_MOTO", "true");
                Log.d("DB_ADD_MOTO", moto.toString());
            } while (cursor.moveToNext());

        } else {
            Log.d("DB_ADD_MOTO", "false");
        }

        cursor.close();
        db.close();
        return list;
    }

    public void dropTableMoto() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE moto");
        String createTableMotoStatement = "CREATE TABLE moto (id NUMERIC, " +
                "user_id NUMERIC, speed INTEGER, latitude VARCHAR, longitude VARCHAR, altitude VARCHAR)";
        db.execSQL(createTableMotoStatement);
    }

    public void dropTableUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE user");
        String createTableUserStatement = "CREATE TABLE IF NOT EXISTS user (id NUMERIC, " +
                "name VARCHAR, nickname VARCHAR, email VARCHAR, status VARCHAR)";
        db.execSQL(createTableUserStatement);
    }

    public List<User> getAllUser() {

        List<User> list = new ArrayList<>();

        String query = "SELECT * FROM user";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do{
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String nickname = cursor.getString(2);
                String email = cursor.getString(3);
                String status = cursor.getString(4);


                User user = new User(id, name, nickname, email, status);
                list.add(user);
                Log.d("DB_ADD_USER", "true");
                Log.d("DB_ADD_USER", user.toString());
            } while (cursor.moveToNext());

        } else {
            Log.d("DB_ADD_USER", "false");
        }

        cursor.close();
        db.close();
        return list;
    }

    public User getUSerById(long id){

        String query = "SELECT name, nickname, email, status FROM user WHERE id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = null;

        if (cursor.moveToFirst()){
            if (cursor.moveToFirst()) {
                do{
                    String name = cursor.getString(0);
                    String nickname = cursor.getString(1);
                    String email = cursor.getString(2);
                    String status = cursor.getString(3);


                    user = new User(id, name, nickname, email, status);
                    Log.d("DB_ADD_USER", "true");
                    Log.d("DB_ADD_USER", user.toString());
                } while (cursor.moveToNext());

            } else {
                Log.d("DB_ADD_USER", "false");
            }
        }

        return user;
    }
}