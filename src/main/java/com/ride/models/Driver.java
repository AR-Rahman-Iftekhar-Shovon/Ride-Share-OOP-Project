package com.ride.models;
public class Driver extends User {

    private Vehicle vehicle;
    private boolean available;
    private double earnings;

    public Driver(int id,
                  String name,
                  String phoneNumber,
                  String password,
                  Vehicle vehicle) {

        super(id, name, phoneNumber, password);
        this.vehicle = vehicle;
        this.available = true;
        this.earnings = 0.0;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getEarnings() {
        return earnings;
    }

    public void addEarnings(double fare) {
        earnings += fare;
    }

    @Override
    public String getRole() {
        return "Driver";
    }

    @Override
    public String toString() {
        return super.toString()
                + ", Vehicle: " + vehicle.getVehicleType()
                + ", Maintenance Cost: " + vehicle.getMaintenanceCost() + " Taka"
                + ", Max Capacity: " + vehicle.getMaxPassengerCapacity() + " Person"
                + ", Earnings: " + earnings + " Taka"
                + ", Available: " + available;

    }
}