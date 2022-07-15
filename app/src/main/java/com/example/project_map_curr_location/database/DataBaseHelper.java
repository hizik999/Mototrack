package com.example.project_map_curr_location.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.domain.User;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app1.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных

    private static final String TABLE_MOTO = "moto";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_SPEED = "speed";
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LON = "longitude";
    private static final String COLUMN_ALT = "altitude";


    private static final String TABLE_USER = "user";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_STATUS = "status";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableMotoStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_MOTO +
                " (" + COLUMN_ID + " NUMERIC, " + COLUMN_USER_ID + " NUMERIC, " + COLUMN_SPEED + " INTEGER, " +
                COLUMN_LAT + " VARCHAR, " + COLUMN_LON + " VARCHAR, " + COLUMN_ALT + " VARCHAR)";

        String createTableUserStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" + COLUMN_ID + " NUMERIC, " + COLUMN_NAME + " VARCHAR, "
                + COLUMN_NICKNAME + " VARCHAR, " + COLUMN_EMAIL + " VARCHAR, " + COLUMN_STATUS + " VARCHAR)";

        db.execSQL(createTableMotoStatement);
        db.execSQL(createTableUserStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
        db.close();
    }

    //добавление мотоциклиста
    public boolean addOne(Moto1 moto) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, moto.getId());
        cv.put(COLUMN_USER_ID, moto.getUser().getId());
        cv.put(COLUMN_SPEED, moto.getSpeed());
        cv.put(COLUMN_LAT, moto.getLatitude());
        cv.put(COLUMN_LON, moto.getLongitude());
        cv.put(COLUMN_ALT, moto.getAltitude());

        long insert = db.insert(TABLE_MOTO, null, cv);

        db.close();
        return insert != -1;
    }

    //добавление юзера
    public boolean addOne(User user){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, user.getId());
        cv.put(COLUMN_NAME, user.getName());
        cv.put(COLUMN_NICKNAME, user.getNickname());
        cv.put(COLUMN_EMAIL, user.getEmail());
        cv.put(COLUMN_STATUS, user.getStatus());

        long insert = db.insert(TABLE_USER, null, cv);

        db.close();
        return insert != -1;
    }

    //получение всех мотоциклистов
    public List<Moto1> getAllMoto() {

        List<Moto1> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MOTO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do{
                long id = cursor.getLong(0);
                int speed = cursor.getInt(2);
                double latitude = cursor.getDouble(3);
                double longitude = cursor.getDouble(4);
                double altitude = cursor.getDouble(5);

                Moto1 moto = new Moto1(id, null, speed, latitude, longitude, altitude);
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

    //пересоздание таблицы мотоциклов
    public void dropTableMoto() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_MOTO);
        String createTableMotoStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_MOTO +
                " (" + COLUMN_ID + " NUMERIC, " + COLUMN_USER_ID + " NUMERIC, " + COLUMN_SPEED + " INTEGER, " +
                COLUMN_LAT + " VARCHAR, " + COLUMN_LON + " VARCHAR, " + COLUMN_ALT + " VARCHAR)";
        db.execSQL(createTableMotoStatement);
    }

    //пересоздание таблицы юзеров
    public void dropTableUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_USER);
        String createTableUserStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" + COLUMN_ID + " NUMERIC, " + COLUMN_NAME + " VARCHAR, "
                + COLUMN_NICKNAME + " VARCHAR, " + COLUMN_EMAIL + " VARCHAR, " + COLUMN_STATUS + " VARCHAR)";
        db.execSQL(createTableUserStatement);
    }

}