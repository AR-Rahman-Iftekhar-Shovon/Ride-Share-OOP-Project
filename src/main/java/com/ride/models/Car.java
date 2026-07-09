package com.ride.models;


public class Car extends Vehicle {

    private static final double RATE_PER_KM = 15.0;

    public Car(String vehicleId, String model, String licensePlate) {
        super(vehicleId, model, licensePlate);
    }

    @Override
    public double calculateFare(double distance) {
        return distance * RATE_PER_KM;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    @Override
    public double getMaintenanceCost() {
    return 2000.0;
}

    @Override
    public int getMaxPassengerCapacity() {
    return 4; 
}
}