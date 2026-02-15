package com.parkingLot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseConnection {
    private static final String URL = "jdbc:sqlite:src/com/parkingLot/database/parking_lot.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            System.out.println("Database Connected Successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite Driver not found! Did you reload Maven?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: Connection failed! " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS vehicles (" +
                    "plate_number TEXT PRIMARY KEY, " +
                    "vehicle_type TEXT, " +
                    "isVip INTEGER DEFAULT 0)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS fines (" +
                    "fine_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "plate_number TEXT NOT NULL, " +
                    "fine_amount REAL NOT NULL, " +
                    "fine_reason TEXT, " +
                    "is_paid INTEGER DEFAULT 0, " +
                    "fine_date TEXT, " +
                    "FOREIGN KEY (plate_number) REFERENCES vehicles(plate_number))");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS parking_rates (" +
                    "rate_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "spot_type TEXT UNIQUE, " +
                    "hourly_rate REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS parking_lots (" +
                    "lot_id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "type TEXT, " +
                    "total_levels INTEGER)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS parking_spots (" +
                    "spot_id TEXT PRIMARY KEY, " +
                    "lot_id TEXT, " +
                    "floor_number INTEGER, " +
                    "row_number INTEGER, " +
                    "spot_number INTEGER, " +
                    "spot_type TEXT, " +
                    "status INTEGER DEFAULT 0, " +
                    "current_vehicle_plate TEXT, " +
                    "FOREIGN KEY (lot_id) REFERENCES parking_lots(lot_id), " +
                    "FOREIGN KEY (spot_type) REFERENCES parking_rates(spot_type))");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS parking_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "plate_number TEXT, " +
                    "entry_time TEXT, " +
                    "exit_time TEXT, " +
                    "fee_charged REAL, " +
                    "spot_id TEXT)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "plate_number TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "payment_method TEXT NOT NULL, " +
                    "transaction_type TEXT NOT NULL, " +
                    "transaction_date TEXT NOT NULL, " +
                    "FOREIGN KEY (plate_number) REFERENCES vehicles(plate_number))");
            
            System.out.println("Database tables initialized successfully!");
        }
    }
    
}
