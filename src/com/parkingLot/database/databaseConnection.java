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

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS vehicles (" +
                    "plate_number TEXT PRIMARY KEY, " +
                    "vehicle_type TEXT, " +
                    "has_handicapped_card INTEGER DEFAULT 0, " +
                    "is_vip INTEGER DEFAULT 0)");

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
                    "row_number TEXT, " +
                    "spot_number INTEGER, " +
                    "spot_type TEXT, " +
                    "status INTEGER DEFAULT 0, " +
                    "current_vehicle_plate TEXT, " +
                    "reserved_for_plate TEXT, " +
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
            
            System.out.println("✅ Database tables initialized successfully!");

            // Run migrations to update existing tables
            migrateDatabaseSchema(conn);
        }
    }

    private static void migrateDatabaseSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Check if reserved_for_plate column exists in parking_spots
            try {
                stmt.execute("SELECT reserved_for_plate FROM parking_spots LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("🔧 Migrating: Adding reserved_for_plate to parking_spots...");
                stmt.execute("ALTER TABLE parking_spots ADD COLUMN reserved_for_plate TEXT");
                System.out.println("✅ Migration complete: reserved_for_plate added");
            }

            // Check if has_handicapped_card column exists in vehicles
            try {
                stmt.execute("SELECT has_handicapped_card FROM vehicles LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("🔧 Migrating: Adding has_handicapped_card to vehicles...");
                stmt.execute("ALTER TABLE vehicles ADD COLUMN has_handicapped_card INTEGER DEFAULT 0");
                System.out.println("✅ Migration complete: has_handicapped_card added");
            }

            // Check if is_vip column exists in vehicles
            try {
                stmt.execute("SELECT is_vip FROM vehicles LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("🔧 Migrating: Adding is_vip to vehicles...");
                stmt.execute("ALTER TABLE vehicles ADD COLUMN is_vip INTEGER DEFAULT 0");
                System.out.println("✅ Migration complete: is_vip added");
            }
        }
    }
    
}
