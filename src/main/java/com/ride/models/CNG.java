package com.ride.models;
public class CNG extends Vehicle {

    private static final double RATE_PER_KM = 10.0;

    public CNG(String vehicleId, String model, String licensePlate) {
        super(vehicleId, model, licensePlate);
    }

    @Override
    public double calculateFare(double distance) {
        return distance * RATE_PER_KM;
    }

    @Override
    public String getVehicleType() {
        return "CNG";
    }
    @Override
    public double getMaintenanceCost() {
    return 1000.0;
}


    @Override
    public int getMaxPassengerCapacity() {
    return 3;
}
}