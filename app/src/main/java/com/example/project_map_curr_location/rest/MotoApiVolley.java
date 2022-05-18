package com.example.project_map_curr_location.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.database.DataBaseHelper;
import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.domain.mapper.MotoMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MotoApiVolley implements MotoApi {

    public static final String API_TEST = "API_TEST_VOLLEY";
    private final Context context;
    public static final String BASE_URL = "http://192.168.1.110:2022";
    private Response.ErrorListener errorListener;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter userAdapter;


    public MotoApiVolley(Context context) {
        this.context = context;

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(API_TEST, error.toString());
            }
        };
    }

    public MotoApiVolley(Context context, SQLiteDatabase db) {
        this.context = context;
        this.db = db;

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(API_TEST, error.toString());
            }
        };
    }

    @Override
    public void fillMoto() {


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto";

        dataBaseHelper = new DataBaseHelper(context);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dataBaseHelper.dropTableMoto();
                        try {
                            int c = 0;
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Moto1 moto = MotoMapper.motoFromJson(jsonObject);

                                if (((int) calculateDistance(moto.getLatitude(), moto.getLongitude(),
                                        ((MainActivity) context).loadDataFloat("actualCameraPositionLat"),
                                        ((MainActivity) context).loadDataFloat("actualCameraPositionLon")))
                                        < 1000) {
                                    c ++;
                                    ((MainActivity) context).saveDataInt("motoCount", c);
                                    boolean success = dataBaseHelper.addOne(moto);
                                    Log.d("API_TEST_VOLLEY", String.valueOf(success));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                errorListener
        );

        requestQueue.add(arrayRequest);
    }

    @Override
    public void addMoto(Moto1 moto) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fillMoto();
                        getId();
                        Log.d(API_TEST, response);
                    }
                },

                errorListener
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                //params.put("user_id", String.valueOf(moto.getUser().getId()));
                params.put("user_id", String.valueOf(((MainActivity) context).loadDataInt("userId")));
                params.put("speed", String.valueOf(moto.getSpeed()));
                params.put("latitude", String.valueOf(moto.getLatitude()));
                params.put("longitude", String.valueOf(moto.getLongitude()));
                params.put("altitude", String.valueOf(moto.getAltitude()));

                return params;
            }
        };

        requestQueue.add(request);
    }

    @Override
    public void getId() {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto/id";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((MainActivity) context).saveDataInt("motoId", Integer.valueOf(response));
                        Log.d(API_TEST, response);
                    }
                },

                errorListener
        );

        requestQueue.add(request);
    }

    @Override
    public void updateMoto(long id, long idUser, int speed, float latitude, float longitude, float altitude) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto/" + id;

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fillMoto();
                    }
                },
                errorListener
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("user_id", String.valueOf(idUser));
                params.put("speed", String.valueOf(speed));
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("altitude", String.valueOf(altitude));

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    @Override
    public void getDistanceByMotoId(long id) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto/distance/" + id;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((MainActivity) context).saveDataFloat("lastMotoDistance", Float.valueOf(response));
                        Log.d("DISTANCE", String.valueOf(Float.valueOf(response)));
                    }
                },
                errorListener
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("endLat", String.valueOf(((MainActivity) context).loadDataFloat("actualCameraPositionLat")));
                params.put("endLon", String.valueOf(((MainActivity) context).loadDataFloat("actualCameraPositionLon")));
                Log.d("DISTANCE", "true");
                return params;
            }
        };

        requestQueue.add(request);
    }

    @Override
    public void getNameByMoto(long id) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto/name/" + id;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((MainActivity) context).saveDataString("lastMotoName", String.valueOf(response));
                        Log.d("MOTO_NAME", response);
                    }
                },

                errorListener
        );

        requestQueue.add(request);

    }

    @Override
    public void deleteMoto(long id) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto/" + id;

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fillMoto();
                        ((MainActivity) context).saveDataInt("motoId", -1);
                    }
                },
                errorListener
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private double calculateDistance(double Alat, double Alon, float Blat, float Blon) {

        final long EARTH_RADIUS = 6372795;
        double lat1 = Alat * Math.PI / 180;
        double lat2 = Blat * Math.PI / 180;
        double lon1 = Alon * Math.PI / 180;
        double lon2 = Blon * Math.PI / 180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);

        double delta = lon1 - lon2;

        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        double ad = Math.atan2(y, x);

        return ad * EARTH_RADIUS;
    }
}
