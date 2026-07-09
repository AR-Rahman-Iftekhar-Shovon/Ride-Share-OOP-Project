package com.ride.models;

public class Ride {

    private int rideId;

    private Passenger passenger;
    private Driver driver;

    private Vehicle vehicle;

    private String pickupLocation;
    private String dropLocation;

    private double distance;
    private double fare;

    private String requestedVehicleType;

    private RideStatus status;

    public Ride(int rideId,
                Passenger passenger,
                String pickupLocation,
                String dropLocation,
                double distance,
                String requestedVehicleType) {

        this.rideId = rideId;
        this.passenger = passenger;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.distance = distance;
        this.requestedVehicleType = requestedVehicleType;

        this.driver = null;
        this.vehicle = null;
        this.fare = 0.0;
        this.status = RideStatus.PENDING;
    }

    public int getRideId() {
        return rideId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getRequestedVehicleType() {
        return requestedVehicleType;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;

        if (driver != null) {
            this.vehicle = driver.getVehicle();
            this.fare = vehicle.calculateFare(distance);
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

   public void setFare(double fare) {
    this.fare = fare;
}

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {

        String driverName =
                (driver == null) ? "Not Assigned" : driver.getName();

        String vehicleType =
                (vehicle == null) ? "N/A" : vehicle.getVehicleType();

        return "\nRide ID      : " + rideId +
               "\nPassenger    : " + passenger.getName() +
               "\nRequested    : " + requestedVehicleType +
               "\nDriver       : " + driverName +
               "\nVehicle      : " + vehicleType +
               "\nFrom         : " + pickupLocation +
               "\nTo           : " + dropLocation +
               "\nDistance     : " + distance + " km" +
               "\nFare         : " + fare + " Taka" +
               "\nStatus       : " + status;
    }
}