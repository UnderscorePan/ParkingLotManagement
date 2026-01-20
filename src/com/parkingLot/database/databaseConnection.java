package com.parkingLot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {
    private static final String URL = "JDBC:sqlite:src/com/parkingLot/database/parking_lot.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.out.println("Failed to connect DB. Error: " + e.getMessage());
        }
        return conn;
    }
}