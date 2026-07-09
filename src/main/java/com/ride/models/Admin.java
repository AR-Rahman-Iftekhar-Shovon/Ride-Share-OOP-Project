package com.ride.models;
public class Admin extends User {

    public Admin(int id,
                 String name,
                 String phoneNumber,
                 String password) {

        super(id, name, phoneNumber, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String toString() {
        return super.toString();
    }
} 
