package com.ride.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static final String CONFIG_FILE = "db.properties";

    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (InputStream is = Database.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                System.out.println(">>> db.properties not found on classpath.");
                System.out.println(">>> Make sure it's inside src/main/resources/, then run 'mvn clean install' again.");
            } else {
                props.load(is);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");
            }
        } catch (IOException e) {
            System.out.println(">>> Could not read db.properties file: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found! Make sure to add MySQL Connector JAR.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void initializeData() {
        try (Connection conn = getConnection()) {
            System.out.println(">>> Connected to MySQL database successfully.");
        } catch (SQLException e) {
            System.out.println(">>> Database connection failed: " + e.getMessage());
            System.out.println(">>> Please check your MySQL server, and make sure RideShare.sql has been run.");
        }
    }
}