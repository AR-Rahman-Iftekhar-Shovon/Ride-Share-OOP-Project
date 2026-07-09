package com.ride.service;

import com.ride.database.Database;
import com.ride.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RideService {

    // ---------------- REQUEST RIDE ----------------

    public Ride requestRide(Passenger passenger, String pickupLocation, String dropLocation,
                             double distance, String requestedVehicleType) {

        String sql = "INSERT INTO rides (passenger_id, pickup_location, drop_location, distance, requested_vehicle_type, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, passenger.getId());
            stmt.setString(2, pickupLocation);
            stmt.setString(3, dropLocation);
            stmt.setDouble(4, distance);
            stmt.setString(5, requestedVehicleType.toUpperCase());
            stmt.executeUpdate();

            int rideId = -1;
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) rideId = keys.getInt(1);
            }

            return new Ride(rideId, passenger, pickupLocation, dropLocation, distance, requestedVehicleType);

        } catch (SQLException e) {
            System.out.println(">>> Ride Request Failed: " + e.getMessage());
            return null;
        }
    }

    public Ride requestRide(Passenger passenger, String pickupLocation, String dropLocation,
                             double distance, String requestedVehicleType, double discount) {

        System.out.println(">>> Promo Applied! Special " + discount + "% discount for " + passenger.getName());
        return requestRide(passenger, pickupLocation, dropLocation, distance, requestedVehicleType);
    }

    // ---------------- HISTORY / LISTS ----------------

    public List<Ride> getPassengerRideHistory(Passenger passenger) {
        List<Ride> history = new ArrayList<>();
        String sql = "SELECT * FROM rides WHERE passenger_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, passenger.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(buildRide(rs, passenger));
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> Failed to fetch ride history: " + e.getMessage());
        }
        return history;
    }

    public List<Ride> getAvailableRides() {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS passenger_name, u.phone_number AS passenger_phone, u.password AS passenger_password " +
                     "FROM rides r JOIN users u ON r.passenger_id = u.id " +
                     "WHERE r.driver_id IS NULL AND r.status = 'PENDING'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Passenger passenger = new Passenger(rs.getInt("passenger_id"), rs.getString("passenger_name"),
                        rs.getString("passenger_phone"), rs.getString("passenger_password"));
                rides.add(buildRide(rs, passenger));
            }
        } catch (SQLException e) {
            System.out.println(">>> Failed to fetch available rides: " + e.getMessage());
        }
        return rides;
    }

    // ---------------- ACCEPT / COMPLETE / CANCEL ----------------

    public boolean acceptRide(int rideId, Driver driver) {
        String selectSql = "SELECT * FROM rides WHERE ride_id = ? AND status = 'PENDING'";

        try (Connection conn = Database.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, rideId);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (!rs.next()) return false;

                String requestedType = rs.getString("requested_vehicle_type");

                if (!driver.isAvailable() ||
                        !driver.getVehicle().getVehicleType().equalsIgnoreCase(requestedType)) {
                    return false;
                }

                double distance = rs.getDouble("distance");
                double fare = driver.getVehicle().calculateFare(distance);

                try (PreparedStatement updateRideStmt = conn.prepareStatement(
                        "UPDATE rides SET driver_id = ?, status = 'ACCEPTED', fare = ? WHERE ride_id = ?")) {
                    updateRideStmt.setInt(1, driver.getId());
                    updateRideStmt.setDouble(2, fare);
                    updateRideStmt.setInt(3, rideId);
                    updateRideStmt.executeUpdate();
                }

                try (PreparedStatement updateDriverStmt = conn.prepareStatement(
                        "UPDATE drivers_extra SET available = FALSE WHERE driver_id = ?")) {
                    updateDriverStmt.setInt(1, driver.getId());
                    updateDriverStmt.executeUpdate();
                }

                driver.setAvailable(false);
                return true;
            }
        } catch (SQLException e) {
            System.out.println(">>> Accept Ride Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean completeRide(int rideId) {
        String selectSql = "SELECT driver_id, fare FROM rides WHERE ride_id = ? AND status = 'ACCEPTED'";

        try (Connection conn = Database.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, rideId);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (!rs.next()) return false;

                int driverId = rs.getInt("driver_id");
                double fare = rs.getDouble("fare");

                try (PreparedStatement updateRideStmt = conn.prepareStatement(
                        "UPDATE rides SET status = 'COMPLETED' WHERE ride_id = ?")) {
                    updateRideStmt.setInt(1, rideId);
                    updateRideStmt.executeUpdate();
                }

                try (PreparedStatement updateDriverStmt = conn.prepareStatement(
                        "UPDATE drivers_extra SET earnings = earnings + ?, available = TRUE WHERE driver_id = ?")) {
                    updateDriverStmt.setDouble(1, fare);
                    updateDriverStmt.setInt(2, driverId);
                    updateDriverStmt.executeUpdate();
                }

                return true;
            }
        } catch (SQLException e) {
            System.out.println(">>> Complete Ride Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelRide(int rideId) {
        String sql = "UPDATE rides SET status = 'CANCELLED' WHERE ride_id = ? AND status = 'PENDING'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rideId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(">>> Cancel Ride Failed: " + e.getMessage());
            return false;
        }
    }

    // ---------------- ADMIN VIEWS ----------------

    public List<Ride> viewAllRides() {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT r.*, u.name AS passenger_name, u.phone_number AS passenger_phone, u.password AS passenger_password " +
                     "FROM rides r JOIN users u ON r.passenger_id = u.id";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Passenger passenger = new Passenger(rs.getInt("passenger_id"), rs.getString("passenger_name"),
                        rs.getString("passenger_phone"), rs.getString("passenger_password"));
                rides.add(buildRide(rs, passenger));
            }
        } catch (SQLException e) {
            System.out.println(">>> Failed to fetch rides: " + e.getMessage());
        }
        return rides;
    }

    public List<Driver> viewAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT u.*, de.vehicle_id, de.available, de.earnings, v.model, v.license_plate, v.vehicle_type " +
                     "FROM users u JOIN drivers_extra de ON u.id = de.driver_id " +
                     "JOIN vehicles v ON de.vehicle_id = v.vehicle_id WHERE u.role = 'Driver'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = AuthService.buildVehicle(rs.getString("vehicle_type"), rs.getString("vehicle_id"),
                        rs.getString("model"), rs.getString("license_plate"));
                Driver driver = new Driver(rs.getInt("id"), rs.getString("name"),
                        rs.getString("phone_number"), rs.getString("password"), vehicle);
                driver.setAvailable(rs.getBoolean("available"));
                driver.addEarnings(rs.getDouble("earnings"));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            System.out.println(">>> Failed to fetch drivers: " + e.getMessage());
        }
        return drivers;
    }

    public List<Passenger> viewAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'Passenger'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                passengers.add(new Passenger(rs.getInt("id"), rs.getString("name"),
                        rs.getString("phone_number"), rs.getString("password")));
            }
        } catch (SQLException e) {
            System.out.println(">>> Failed to fetch passengers: " + e.getMessage());
        }
        return passengers;
    }

    // ---------------- REPORT ----------------

    public void generateReport() {
        try (Connection conn = Database.getConnection()) {

            int totalPassengers = countByRole(conn, "Passenger");
            int totalDrivers = countByRole(conn, "Driver");

            int totalRides = 0, completedRides = 0, cancelledRides = 0;
            double totalRevenue = 0;

            String sql = "SELECT status, COUNT(*) AS cnt, SUM(fare) AS revenue FROM rides GROUP BY status";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("cnt");
                    totalRides += count;

                    if ("COMPLETED".equals(status)) {
                        completedRides = count;
                        totalRevenue = rs.getDouble("revenue");
                    } else if ("CANCELLED".equals(status)) {
                        cancelledRides = count;
                    }
                }
            }

            System.out.println("\n=================================");
            System.out.println("          SYSTEM REPORT");
            System.out.println("=================================");
            System.out.println("Total Passengers : " + totalPassengers);
            System.out.println("Total Drivers    : " + totalDrivers);
            System.out.println("Total Rides      : " + totalRides);
            System.out.println("Completed Rides  : " + completedRides);
            System.out.println("Cancelled Rides  : " + cancelledRides);
            System.out.println("Total Revenue    : " + totalRevenue + " Taka");
            System.out.println("=================================");

        } catch (SQLException e) {
            System.out.println(">>> Failed to generate report: " + e.getMessage());
        }
    }

    private int countByRole(Connection conn, String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ---------------- HELPERS ----------------

    // rides table-er ekta row theke Ride object banay (driver thakle driver soho)
    private Ride buildRide(ResultSet rs, Passenger passenger) throws SQLException {
        Ride ride = new Ride(
                rs.getInt("ride_id"),
                passenger,
                rs.getString("pickup_location"),
                rs.getString("drop_location"),
                rs.getDouble("distance"),
                rs.getString("requested_vehicle_type")
        );

        int driverId = rs.getInt("driver_id");
        if (!rs.wasNull()) {
            Driver driver = loadDriverById(driverId);
            if (driver != null) {
                ride.setDriver(driver);
            }
        }

        ride.setStatus(RideStatus.valueOf(rs.getString("status")));
        ride.setFare(rs.getDouble("fare")); 

        return ride;
    }

    private Driver loadDriverById(int driverId) throws SQLException {
        String sql = "SELECT u.*, de.vehicle_id, de.available, de.earnings, v.model, v.license_plate, v.vehicle_type " +
                     "FROM users u JOIN drivers_extra de ON u.id = de.driver_id " +
                     "JOIN vehicles v ON de.vehicle_id = v.vehicle_id WHERE u.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = AuthService.buildVehicle(rs.getString("vehicle_type"), rs.getString("vehicle_id"),
                            rs.getString("model"), rs.getString("license_plate"));
                    Driver driver = new Driver(rs.getInt("id"), rs.getString("name"),
                            rs.getString("phone_number"), rs.getString("password"), vehicle);
                    driver.setAvailable(rs.getBoolean("available"));
                    driver.addEarnings(rs.getDouble("earnings"));
                    return driver;
                }
            }
        }
        return null;
    }
}