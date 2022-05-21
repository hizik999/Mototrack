package com.example.project_map_curr_location.rest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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
import com.example.project_map_curr_location.domain.User;
import com.example.project_map_curr_location.domain.mapper.UserMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserApiVolley implements UserApi{

    public static final String API_TEST = "API_TEST";
    private final Context context;
    public static final String BASE_URL = "http://192.168.1.110:2022";
    private Response.ErrorListener errorListener;
    private ArrayList<User> arrayList;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    private RequestQueue requestQueue;

    public UserApiVolley(Context context) {
        this.context = context;

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(API_TEST, error.toString());
                Toast.makeText(context, "Нет подключения к Интернету, \nданные не актуальны", 5).show();
            }
        };
        arrayList = new ArrayList<>();
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

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

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

                        Log.d(API_TEST, arrayList.toString());
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

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getNewId();
                        fillUser();
                        Log.d("API_TEST_ADD_USER", response);
                    }
                },

                errorListener
        )
                // ВОТ ЭТА ХУЙНЯ НЕ РАБОТАЕТ ПОТОМУ ЧТО КОНТРОЛЛЕР ТРЕБУЕТ ПАРАМЕТРЫ НЕ СТРИНГ А ДРУГИЕ
                // А КАК ЭТО ПОМЕНЯТЬ Я НЕ ЕБУ ОТ СЛОВА СОВСЕМ ПОМОГИТЕ

                // я понял в чем ошибка: нужен user_id от созданного user, без него работать не будет
                // исправление нужно дописать в UserApiVolley, где уже будет браться user и вставляться в moto
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                //params.put("id", String.valueOf(((MainActivity) context).loadDataInt("userId")));
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fillUser();
                    }
                },
                errorListener
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

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

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((MainActivity) context).saveDataInt("userId", Integer.valueOf(response));
                        Log.d(API_TEST, response);
                    }
                },

                errorListener
        );

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        return 0;
    }
}
