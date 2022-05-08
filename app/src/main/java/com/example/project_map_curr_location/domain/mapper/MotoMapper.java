package com.example.project_map_curr_location.domain.mapper;

import com.example.project_map_curr_location.domain.Moto1;

import org.json.JSONException;
import org.json.JSONObject;

public class MotoMapper {

    public static Moto1 motoFromJson(JSONObject jsonObject){

        Moto1 moto = null;
        try {
            moto = new Moto1(
                    jsonObject.getLong("id"),
                    UserMapper.userFromMotoJson(jsonObject),
                    jsonObject.getInt("speed"),
                    jsonObject.getDouble("latitude"),
                    jsonObject.getDouble("longitude"),
                    jsonObject.getDouble("altitude")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moto;
    }
}
