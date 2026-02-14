package com.parkingLot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {
    private static final String URL = "jdbc:sqlite:src/com/parkingLot/database/parking_lot.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            System.out.println("✅ Database Connected Successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: SQLite Driver not found! Did you reload Maven?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error: Connection failed! " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}