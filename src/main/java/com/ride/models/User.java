package com.ride.models;
public abstract class User {

    private int id;
    private String name;
    private String phoneNumber;
    private String password;


    public User(int id, String name, String phoneNumber, String password) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    /**
     * Returns user ID.
     *
     * @return user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns user name.
     *
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets user name.
     *
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns phone number.
     *
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Polymorphism Demonstration
     * Each child class will provide its own role.
     *
     * @return role name
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return "ID: " + id +
               ", Name: " + name +
               ", Phone: " + phoneNumber +
               ", Role: " + getRole();
    }
}