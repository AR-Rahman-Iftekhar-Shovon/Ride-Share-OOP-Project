package com.ride.models;

public abstract class Vehicle {

    private String vehicleId;
    private String model;
    private String licensePlate;

    public Vehicle(String vehicleId, String model, String licensePlate) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.licensePlate = licensePlate;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

   
    public abstract double getMaintenanceCost(); 
    public abstract int getMaxPassengerCapacity();
    public abstract double calculateFare(double distance);

    /**
     * Returns vehicle type.
     */
    public abstract String getVehicleType();

    @Override
    public String toString() {
        return "Vehicle ID: " + vehicleId
                + ", Model: " + model
                + ", License Plate: " + licensePlate
                + ", Type: " + getVehicleType()
                + ", Maintenance Cost: " + getMaintenanceCost() + " Taka"
                + ", Max Capacity: " + getMaxPassengerCapacity() + " Person";
    }
}