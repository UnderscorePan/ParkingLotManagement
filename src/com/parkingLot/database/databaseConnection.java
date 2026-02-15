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
                    "vehicle_type TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS fines (" +
                    "fine_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "plate_number TEXT NOT NULL, " +
                    "fine_amount REAL NOT NULL, " +
                    "fine_type TEXT, " +
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
            
            // Initialize default parking lot
            initializeDefaultParkingLot(conn);
        }
    }
    
    private static void initializeDefaultParkingLot(Connection conn) throws SQLException {
        // Check if default parking lot exists
        String checkSql = "SELECT COUNT(*) FROM parking_lots WHERE lot_id = 'LOT-001'";
        try (Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert default parking lot
                String insertSql = "INSERT INTO parking_lots (lot_id, name, type, total_levels) " +
                                 "VALUES ('LOT-001', 'Main Parking Lot', 'Multi-Level', 10)";
                stmt.execute(insertSql);
                System.out.println("✅ Default parking lot (LOT-001) initialized");
            }
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
            
            // Check if parking_fee column exists in transactions
            try {
                stmt.execute("SELECT parking_fee FROM transactions LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("🔧 Migrating: Adding parking_fee to transactions...");
                stmt.execute("ALTER TABLE transactions ADD COLUMN parking_fee REAL DEFAULT 0");
                System.out.println("✅ Migration complete: parking_fee added");
            }
            
            // Check if fine_amount column exists in transactions
            try {
                stmt.execute("SELECT fine_amount FROM transactions LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("🔧 Migrating: Adding fine_amount to transactions...");
                stmt.execute("ALTER TABLE transactions ADD COLUMN fine_amount REAL DEFAULT 0");
                System.out.println("✅ Migration complete: fine_amount added");
            }
            
            // Check if fine_type column exists in fines
            try {
                stmt.execute("SELECT fine_type FROM fines LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("🔧 Migrating: Adding fine_type to fines...");
                stmt.execute("ALTER TABLE fines ADD COLUMN fine_type TEXT DEFAULT 'Fixed Fine Scheme'");
                System.out.println("✅ Migration complete: fine_type added");
            }
        }
    }
    
}
