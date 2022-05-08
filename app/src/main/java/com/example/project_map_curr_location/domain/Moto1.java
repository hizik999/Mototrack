package com.example.project_map_curr_location.domain;

public class Moto1 {

    private long id;
    private User user;
    private int speed;
    private double latitude;
    private double longitude;
    private double altitude;

    public Moto1(long id, User user, int speed, double latitude, double longitude, double altitude) {
        this.id = id;
        this.user = user;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public Moto1(User user, int speed, double latitude, double longitude, double altitude) {
        this.user = user;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "Moto1{" +
                "id=" + id +
                ", user=" + user +
                ", speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}
