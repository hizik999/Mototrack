package com.example.project_map_curr_location.rest;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.database.DataBaseHelper;
import com.example.project_map_curr_location.domain.User;
import com.example.project_map_curr_location.domain.mapper.UserMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserApiVolley implements UserApi{

    public static final String API_TEST = "API_TEST";
    private final Context context;
    public static final String BASE_URL = "http://78.40.217.59:2022";
    private final Response.ErrorListener errorListener;

    private DataBaseHelper dataBaseHelper;

    private RequestQueue requestQueue;

    public UserApiVolley(Context context) {
        this.context = context;

        errorListener = error -> Log.d(API_TEST, error.toString());
    }

    @Override
    public void fillUser() {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = BASE_URL + "/user";

        dataBaseHelper = new DataBaseHelper(context);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,

                response -> {

                    dataBaseHelper.dropTableUser();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            User user = UserMapper.userFromJson(jsonObject);

                            if (user.getId() == ((MainActivity) context).loadDataInt("userId")){
                                boolean success = dataBaseHelper.addOne(user);
                                Log.d("API_TEST_VOLLEY", String.valueOf(success));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                errorListener
        );

        requestQueue.add(arrayRequest);
    }


    @Override
    public void addUser(User user) {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = BASE_URL + "/user";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {
                    getNewId();
                    fillUser();
                    Log.d("API_TEST_ADD_USER", response);
                },

                errorListener
        )
        {
            @NonNull
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("name", "");
                params.put("nickname", String.valueOf(((MainActivity) context).loadDataString("userNickname")));
                params.put("email", "");
                params.put("status", String.valueOf(user.getStatus()));

                Log.d("addUser", "true");
                return params;
            }
        };

        requestQueue.add(request);

    }

    @Override
    public void updateUser(long id, String name, String nickname, String email, String status) {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = BASE_URL + "/user/" + id;

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                response -> fillUser(),
                errorListener
        ){
            @NonNull
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("name", String.valueOf(name));
                params.put("nickname", String.valueOf(nickname));
                params.put("email", String.valueOf(email));
                params.put("status", String.valueOf(status));

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public long getNewId() {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = BASE_URL + "/user/id";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                response -> {
                    ((MainActivity) context).saveDataInt("userId", Integer.parseInt(response));
                    Log.d(API_TEST, response);
                },

                errorListener
        );

        requestQueue.add(request);

        return ((MainActivity) context).loadDataInt("userId");
    }
}
