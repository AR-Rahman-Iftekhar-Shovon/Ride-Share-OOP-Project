package com.ride.service;

import com.ride.database.Database;
import com.ride.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthService {

    // ---------------- REGISTRATION ----------------

    public boolean registerPassenger(String name, String phone, String password) {
        String insertUserSQL = "INSERT INTO users (name, phone_number, password, role) VALUES (?, ?, ?, 'Passenger')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertUserSQL)) {

            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, password);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println(">>> Passenger Registration Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean registerDriver(String name, String phone, String password,
                                   String vehicleId, String model, String licensePlate, String vehicleType) {

        String insertUserSQL = "INSERT INTO users (name, phone_number, password, role) VALUES (?, ?, ?, 'Driver')";
        String insertVehicleSQL = "INSERT INTO vehicles (vehicle_id, model, license_plate, vehicle_type) VALUES (?, ?, ?, ?)";
        String insertDriverExtraSQL = "INSERT INTO drivers_extra (driver_id, vehicle_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtVehicle = conn.prepareStatement(insertVehicleSQL)) {
                stmtVehicle.setString(1, vehicleId);
                stmtVehicle.setString(2, model);
                stmtVehicle.setString(3, licensePlate);
                stmtVehicle.setString(4, vehicleType.toUpperCase());
                stmtVehicle.executeUpdate();
            }

            int driverId = -1;
            try (PreparedStatement stmtUser = conn.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, name);
                stmtUser.setString(2, phone);
                stmtUser.setString(3, password);
                stmtUser.executeUpdate();

                try (ResultSet generatedKeys = stmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        driverId = generatedKeys.getInt(1);
                    }
                }
            }

            if (driverId != -1) {
                try (PreparedStatement stmtExtra = conn.prepareStatement(insertDriverExtraSQL)) {
                    stmtExtra.setInt(1, driverId);
                    stmtExtra.setString(2, vehicleId);
                    stmtExtra.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println(">>> Driver Registration Failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println(">>> Transaction rolled back successfully.");
                } catch (SQLException ex) {
                    System.out.println(">>> Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ---------------- LOGIN ----------------
    // Age eguloi Main.java-te Database.passengers/drivers/admins list loop kore hoto.
    // Ekhon MySQL query diye hocche.

    public Passenger loginPassenger(String phone, String password) {
        String sql = "SELECT * FROM users WHERE phone_number = ? AND password = ? AND role = 'Passenger'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Passenger(rs.getInt("id"), rs.getString("name"),
                            rs.getString("phone_number"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> Login Failed: " + e.getMessage());
        }
        return null;
    }

    public Driver loginDriver(String phone, String password) {
        String sql = "SELECT u.*, de.vehicle_id, de.available, de.earnings, " +
                     "v.model, v.license_plate, v.vehicle_type " +
                     "FROM users u " +
                     "JOIN drivers_extra de ON u.id = de.driver_id " +
                     "JOIN vehicles v ON de.vehicle_id = v.vehicle_id " +
                     "WHERE u.phone_number = ? AND u.password = ? AND u.role = 'Driver'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = buildVehicle(
                            rs.getString("vehicle_type"),
                            rs.getString("vehicle_id"),
                            rs.getString("model"),
                            rs.getString("license_plate"));

                    Driver driver = new Driver(rs.getInt("id"), rs.getString("name"),
                            rs.getString("phone_number"), rs.getString("password"), vehicle);
                    driver.setAvailable(rs.getBoolean("available"));
                    driver.addEarnings(rs.getDouble("earnings"));
                    return driver;
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> Login Failed: " + e.getMessage());
        }
        return null;
    }

    public Admin loginAdmin(String phone, String password) {
        String sql = "SELECT * FROM users WHERE phone_number = ? AND password = ? AND role = 'Admin'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(rs.getInt("id"), rs.getString("name"),
                            rs.getString("phone_number"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.out.println(">>> Login Failed: " + e.getMessage());
        }
        return null;
    }

    // vehicle_type string theke shothik Car/Bike/CNG object banay
    public static Vehicle buildVehicle(String type, String vehicleId, String model, String licensePlate) {
        switch (type.toUpperCase()) {
            case "CAR":
                return new Car(vehicleId, model, licensePlate);
            case "BIKE":
                return new Bike(vehicleId, model, licensePlate);
            case "CNG":
                return new CNG(vehicleId, model, licensePlate);
            default:
                return null;
        }
    }
}