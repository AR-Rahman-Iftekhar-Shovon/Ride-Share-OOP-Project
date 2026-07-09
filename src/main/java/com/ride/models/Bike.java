package com.ride.models;
public class Bike extends Vehicle {

    private static final double RATE_PER_KM = 8.0;

    public Bike(String vehicleId, String model, String licensePlate) {
        super(vehicleId, model, licensePlate);
    }

    @Override
    public double calculateFare(double distance) {
        return distance * RATE_PER_KM;
    }

    @Override
    public String getVehicleType() {
        return "Bike";
    }

    @Override
    public double getMaintenanceCost() {
    return 500.0;
    
}


    @Override
    public int getMaxPassengerCapacity() {
    return 1;
}
}