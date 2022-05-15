package com.example.project_map_curr_location.rest;

import com.example.project_map_curr_location.domain.User;

public interface UserApi {

    void fillUser();

    void addUser(User user);

    void updateUser(
            long id,
            String name,
            String nickname,
            String email,
            String status
    );

    long getNewId();
}
