package com.example.project_map_curr_location.domain.mapper;

import com.example.project_map_curr_location.domain.User;
import org.json.JSONException;
import org.json.JSONObject;

public class UserMapper {

    public static User userFromJson(JSONObject jsonObject){

        User user = null;
        try {
            user = new User(
                    jsonObject.getLong("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("nickname"),
                    jsonObject.getString("email"),
                    jsonObject.getString("status")
            );
        } catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }

    public static User userFromMotoJson(JSONObject jsonObject){

        User user = null;
        try {
            user = userFromJson(jsonObject.getJSONObject("userDto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
