package com.ride.models;
public class Passenger extends User {

    public Passenger(int id, String name, String phoneNumber, String password) {
        super(id, name, phoneNumber, password);
    }

    @Override
    public String getRole() {
        return "Passenger";
    }
}