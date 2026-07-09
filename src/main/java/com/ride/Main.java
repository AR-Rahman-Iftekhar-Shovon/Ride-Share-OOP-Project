package com.ride;
import com.ride.database.Database;
import com.ride.service.AuthService;
import com.ride.service.RideService;  
import com.ride.models.*;
import java.util.Scanner;

public class Main {
    private static AuthService authService = new AuthService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final RideService rideService = new RideService();


    public static void main(String[] args) {

        Database.initializeData();

        while (true) {

            System.out.println("\n=================================");
            System.out.println("          LET'S RIDE      ");
            System.out.println("=================================");
            System.out.println("1. Passenger Login");
            System.out.println("2. Driver Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Register (New User)");
            System.out.println("5. Exit");
            System.out.println("=================================");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    passengerLogin();
                    break;

                case 2:
                    driverLogin();
                    break;

                case 3:
                    adminLogin();
                    break;

                case 4:
                    handleRegistration(scanner);
                    break;

                case 5:
                    System.out.println("Thank you for using the system.");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleRegistration(Scanner scanner) {
    System.out.println("\n========== REGISTRATION MENU ==========");
    System.out.println("1. Register as Passenger");
    System.out.println("2. Register as Driver");
    System.out.print("Choose option: ");
    
    int regChoice = scanner.nextInt();
    scanner.nextLine(); // Buffer clear

    if (regChoice == 1) {
        // প্যাসেঞ্জার রেজিষ্ট্রেশন ইনপুট
        System.out.println("\n--- Passenger Signup ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        boolean success = authService.registerPassenger(name, phone, password);
        if (success) {
            System.out.println("\n>>> Passenger Registered Successfully! You can now login.");
        } else {
            System.out.println("\n>>> Registration Failed. Phone number might already exist.");
        }

    } else if (regChoice == 2) {
        // ড্রাইভার ও তার গাড়ির রেjiষ্ট্রেশন ইনপুট
        System.out.println("\n--- Driver Signup ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("\n--- Vehicle Details ---");
        System.out.print("Enter Vehicle ID (e.g., CAR01, BIKE99): ");
        String vehicleId = scanner.nextLine();
        System.out.print("Enter Vehicle Model (e.g., Toyota Axio): ");
        String model = scanner.nextLine();
        System.out.print("Enter License Plate (e.g., DHAKA-METRO-12-3456): ");
        String licensePlate = scanner.nextLine();
        System.out.print("Enter Vehicle Type (CAR/BIKE/CNG): ");
        String vehicleType = scanner.nextLine().toUpperCase();

        boolean success = authService.registerDriver(name, phone, password, vehicleId, model, licensePlate, vehicleType);
        if (success) {
            System.out.println("\n>>> Driver and Vehicle Registered Successfully!");
        } else {
            System.out.println("\n>>> Registration Failed. Please check if ID or License Plate is duplicate.");
        }
    } else {
        System.out.println("Invalid Choice.");
    }




    }

    private static void passengerLogin() {

        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Passenger passenger = authService.loginPassenger(phone, password);

        if (passenger != null) {
            System.out.println("\nPassenger Login Successful");
            passengerMenu(passenger);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private static void driverLogin() {

        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Driver driver = authService.loginDriver(phone, password);

        if (driver != null) {
            System.out.println("\nDriver Login Successful");
            driverMenu(driver);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private static void adminLogin() {

        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Admin admin = authService.loginAdmin(phone, password);

        if (admin != null) {
            System.out.println("\nAdmin Login Successful");
            adminMenu(admin);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    // Part 2
    private static void passengerMenu(Passenger passenger) {
    while (true) {

        System.out.println("\n=================================");
        System.out.println("         PASSENGER MENU");
        System.out.println("=================================");
        System.out.println("1. Request a Ride");
        System.out.println("2. View My Ride History");
        System.out.println("3. Cancel a Ride");
        System.out.println("4. Logout");
        System.out.println("=================================");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {

            case 1:
                System.out.print("Pickup Location: ");
                String pickup = scanner.nextLine();

                System.out.print("Drop Location: ");
                String drop = scanner.nextLine();

                System.out.print("Distance (km): ");
                double distance = scanner.nextDouble();
                scanner.nextLine(); 

                System.out.print("Vehicle Type (CAR/BIKE/CNG): ");
                String vehicleType = scanner.nextLine().toUpperCase(); 

                System.out.print("Enter Discount Percentage (Enter 0 if no promo): ");
                double discount = scanner.nextDouble();
                scanner.nextLine();

                if (discount > 0) {
                    
                    rideService.requestRide(
                            passenger,
                            pickup,
                            drop,
                            distance,
                            vehicleType,
                            discount
                    );
                } else {
                   
                    rideService.requestRide(
                            passenger,
                            pickup,
                            drop,
                            distance,
                            vehicleType
                    );
                }
               

                System.out.println("\nRide Requested Successfully!");
                break;

            case 2:
                System.out.println("\n========== MY RIDES ==========");
                boolean found = false;

                for (var ride : rideService.getPassengerRideHistory(passenger)) {
                    System.out.println(ride);
                    System.out.println("-----------------------------");
                    found = true;
                }

                if (!found) {
                    System.out.println("No ride history found.");
                }
                break;

            case 3:
                System.out.print("Enter Ride ID to cancel: ");
                int rideId = scanner.nextInt();
                scanner.nextLine();

                boolean cancelled = rideService.cancelRide(rideId);

                if (cancelled) {
                    System.out.println("Ride cancelled successfully.");
                } else {
                    System.out.println("Ride cannot be cancelled.");
                }
                break;

            case 4:
                System.out.println("Logging out...");
                return;

            default:
                System.out.println("Invalid choice.");
        }
    }
}
    

    // Part 3
private static void driverMenu(Driver driver) {

    while (true) {

        System.out.println("\n=================================");
        System.out.println("          DRIVER MENU");
        System.out.println("=================================");
        System.out.println("1. View Available Rides");
        System.out.println("2. Accept a Ride");
        System.out.println("3. Complete Current Ride");
        System.out.println("4. View My Earnings");
        System.out.println("5. Logout");
        System.out.println("=================================");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {

            case 1:

                System.out.println("\n====== AVAILABLE RIDES ======");

                boolean found = false;

                for (Ride ride : rideService.getAvailableRides()) {

                    System.out.println(ride);
                    System.out.println("----------------------------");

                    found = true;
                }

                if (!found) {
                    System.out.println("No available rides.");
                }

                break;

            case 2:

                System.out.print("Enter Ride ID to accept: ");
                int acceptRideId = scanner.nextInt();
                scanner.nextLine();

                boolean accepted =
                        rideService.acceptRide(
                                acceptRideId,
                                driver
                        );

                if (accepted) {
                    System.out.println("Ride accepted successfully.");
                } else {
                    System.out.println("Ride cannot be accepted.");
                }

                break;

            case 3:

                System.out.print("Enter Ride ID to complete: ");
                int completeRideId = scanner.nextInt();
                scanner.nextLine();

                boolean completed =
                        rideService.completeRide(
                                completeRideId
                        );

                if (completed) {
                    System.out.println("Ride completed successfully.");
                } else {
                    System.out.println("Ride cannot be completed.");
                }

                break;

            case 4:

                System.out.println(
                        "\nTotal Earnings: " + 
                                + driver.getEarnings() + " Taka"
                );

                break;

            case 5:

                System.out.println("Logging out...");
                return;

            default:

                System.out.println("Invalid choice.");
        }
    }
}

    // Part 4
   private static void adminMenu(Admin admin) {

    while (true) {

        System.out.println("\n=================================");
        System.out.println("           ADMIN MENU");
        System.out.println("=================================");
        System.out.println("1. View All Users");
        System.out.println("2. View All Rides");
        System.out.println("3. View All Drivers");
        System.out.println("4. Generate Report");
        System.out.println("5. Logout");
        System.out.println("=================================");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {

            case 1:

                System.out.println("\n========== PASSENGERS ==========");

                for (Passenger passenger : rideService.viewAllPassengers()) {
                    System.out.println(passenger);
                }

                break;

            case 2:

                System.out.println("\n========== RIDES ==========");

                for (Ride ride : rideService.viewAllRides()) {
                    System.out.println(ride);
                    System.out.println("-------------------------");
                }

                break;

            case 3:

                System.out.println("\n========== DRIVERS ==========");

                for (Driver driver : rideService.viewAllDrivers()) {
                    System.out.println(driver);
                }

                break;

            case 4:

                rideService.generateReport();
                break;

            case 5:

                System.out.println("Logging out...");
                return;

            default:

                System.out.println("Invalid choice.");
        }
    }
}
}