🚗 Let's_Ride — Ride Sharing System (Java + OOP + MySQL)

A console-based ride-sharing platform built in Java, demonstrating core Object-Oriented Programming principles with a real MySQL database backend. Built as a 2nd semester OOP course project.


📌 Overview

LetsRide simulates a simplified version of a ride-sharing service (like Uber/Pathao) where Passengers can request rides, Drivers can accept and complete them, and Admins can monitor the whole system — all backed by a persistent MySQL database.


✨ Features


User Registration & Login for Passengers and Drivers, with role-based Admin access
Ride Request with pickup/drop location, distance, and vehicle type
Ride Lifecycle Management — Request → Accept → Complete / Cancel
Fare Calculation based on vehicle type (Car / Bike / CNG), each with its own per-km rate
Driver Earnings Tracking
Admin Dashboard — view all users, all rides, all drivers, and generate a system-wide report
Persistent Storage — all data is saved in MySQL, not lost when the program closes



🧠 OOP Concepts Used

ConceptWhere it's usedEncapsulationAll model classes (User, Vehicle, Ride) keep fields private, exposed only via getters/settersInheritancePassenger, Driver, Admin extend User; Car, Bike, CNG extend VehiclePolymorphismgetRole() and calculateFare() are overridden differently in each subclass (runtime polymorphism); requestRide() is overloaded (compile-time polymorphism)AbstractionUser and Vehicle are abstract classes — they define a contract but can never be instantiated directlyCompositionDriver has-a Vehicle; Ride has-a Passenger and Driver


🏗️ Architecture

The project follows a simple layered architecture:

com.ride
├── Main.java                 → Console UI / entry point
├── models/                   → Domain classes (User, Vehicle, Ride, etc.)
├── service/                  → Business logic (AuthService, RideService)
└── database/                 → Database connection handling


Models — plain OOP classes, no database logic
Service layer — talks to the database via JDBC, keeps SQL out of Main.java
Database layer — a single place responsible for the MySQL connection



🛠️ Tech Stack


Java (JDK 21)
MySQL (relational database)
JDBC (mysql-connector-j) for database connectivity
Maven for dependency management and build



🗄️ Database Schema

TablePurposeusersStores Passengers, Drivers, and Admins (differentiated by a role column)vehiclesVehicle details (Car / Bike / CNG)drivers_extraExtra driver-specific data — assigned vehicle, availability, earningsridesEvery ride request, its status, and fare

Full schema: RideShare.sql


🚀 Getting Started

Prerequisites


JDK 21+
MySQL Server running locally
Maven


Setup


Clone the repo


bash   git clone https://github.com/<your-username>/letsride.git
cd letsride


Create the database
bash   mysql -u root -p < RideShare.sql


Configure your database credentials
Copy the example config and fill in your own MySQL credentials:


bash   cp db.properties.example db.properties

Edit db.properties:

properties   db.url=jdbc:mysql://localhost:3306/ride_sharing_db
   db.user=root
   db.password=your_password_here


db.properties is git-ignored — your real credentials never get committed.




Build and run


bash   mvn clean install
       mvn compile exec:java


📋 Sample Menu Flow

=================================
          LET'S RIDE      
=================================
1. Passenger Login
2. Driver Login
3. Admin Login
4. Register (New User)
5. Exit
=================================


🔮 Future Improvements


Password hashing (currently stored as plain text — fine for a learning project, not for production)
Unit tests with JUnit
REST API layer instead of console I/O
Ride rating/review system



📄 License

This project is licensed under the MIT License — see the LICENSE file for details.


👤 Author

AR Rahman Iftekhar Shovon
2nd Semester — Object-Oriented Programming Course Project
