package com.example.project_map_curr_location;

public class User {

    private long id;
    private String name;
    private String nickname;
    private String email;
    private String status;

    public User(long id, String name, String nickname, String email, String status) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
