package com.ride.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    // db.properties file theke connection info load kora hoy.
    // Ei file-ta .gitignore-e thakbe, tai GitHub-e password kokhono jabe na.
    private static final String CONFIG_FILE = "db.properties";

    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            System.out.println(">>> Could not read db.properties file.");
            System.out.println(">>> Copy db.properties.example to db.properties and fill in your own credentials.");
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