CREATE DATABASE IF NOT EXISTS ride_sharing_db;
USE ride_sharing_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Passenger', 'Driver', 'Admin') NOT NULL
);
select* from users;
CREATE TABLE vehicles (
    vehicle_id VARCHAR(50) PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    license_plate VARCHAR(50) UNIQUE NOT NULL,
    vehicle_type ENUM('CAR', 'BIKE', 'CNG') NOT NULL
);
select* from vehicles;

CREATE TABLE drivers_extra (
    driver_id INT PRIMARY KEY,
    vehicle_id VARCHAR(50),
    available BOOLEAN DEFAULT TRUE,
    earnings DOUBLE DEFAULT 0.0,
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE SET NULL
);
select *from drivers_extra;
-- NEW: rides table
CREATE TABLE rides (
    ride_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_id INT NOT NULL,
    driver_id INT,
    pickup_location VARCHAR(150) NOT NULL,
    drop_location VARCHAR(150) NOT NULL,
    distance DOUBLE NOT NULL,
    requested_vehicle_type VARCHAR(20) NOT NULL,
    fare DOUBLE DEFAULT 0.0,
    status ENUM('PENDING', 'ACCEPTED', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE SET NULL
);

INSERT INTO users (name, phone_number, password, role)
VALUES ('Akhi', '01701901001', 'akhi', 'Admin');

