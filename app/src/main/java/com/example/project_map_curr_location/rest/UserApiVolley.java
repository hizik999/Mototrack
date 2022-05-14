package com.example.project_map_curr_location.rest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_map_curr_location.database.DataBaseHelper;
import com.example.project_map_curr_location.domain.User;
import com.example.project_map_curr_location.domain.mapper.UserMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserApiVolley implements UserApi{

    public static final String API_TEST = "API_TEST";
    private final Context context;
    public static final String BASE_URL = "http://192.168.1.108:2022";
    private Response.ErrorListener errorListener;
    private ArrayList<User> arrayList;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    public UserApiVolley(Context context) {
        this.context = context;

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(API_TEST, error.toString());
            }
        };
        arrayList = new ArrayList<>();
    }

    @Override
    public void fillUser() {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/user";

        dataBaseHelper = new DataBaseHelper(context);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        dataBaseHelper.dropTableUser();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                User user = UserMapper.userFromJson(jsonObject);
                                boolean success = dataBaseHelper.addOne(user);
                                Log.d("API_TEST_VOLLEY", String.valueOf(success));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(API_TEST, arrayList.toString());
                    }
                },

                errorListener
        );

        requestQueue.add(arrayRequest);
    }


    @Override
    public void addUser(User user) {

    }

    @Override
    public void updateUser(long id, String name, String nickname, String email, String status) {

    }
}
