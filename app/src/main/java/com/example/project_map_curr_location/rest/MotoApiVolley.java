package com.example.project_map_curr_location.rest;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.domain.mapper.MotoMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MotoApiVolley implements MotoApi {

    public static final String API_TEST = "API_TEST";
    private final Context context;
    public static final String BASE_URL = "http://192.168.1.108:2022";
    private Response.ErrorListener errorListener;
    private ArrayList<Moto1> arrayList;

    public MotoApiVolley(Context context) {
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
    public void fillMoto() {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BASE_URL + "/moto";

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Moto1 moto = MotoMapper.motoFromJson(jsonObject);
                                arrayList.add(moto);
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
                        Log.d(API_TEST, response);
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

                //params.put("user_id", String.valueOf(moto.getUser().getId()));
                params.put("user_id", String.valueOf(moto.getUser().getId()));
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
    public void updateMoto(long id, long idUser, int speed, float latitude, float longitude, float altitude) {

    }

    @Override
    public void deleteMoto(long id) {

    }
}
