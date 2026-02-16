# Parking Lot Management System

CCP6224 - Object-Oriented Analysis and Design

---

## Group Members

- Hew Wee Bo
- Shanjif Cakravathi
- Merey Abilkhan
- Liaw Yong Loon

---

## Features

- Multi-level parking lot structure
- Vehicle entry/exit management
- Fine calculation (3 schemes: Fixed, Hourly, Progressive)
- Payment processing
- Admin and reporting panels

---

## Technology Stack

- **Language**: Java 17
- **Build Tool**: Maven 3.6+
- **Database**: SQLite 3.47.1.0
- **GUI Framework**: Java Swing

---

## Project Structure

```
ParkingLotManagement/
├── pom.xml                    # Maven configuration
├── src/                       # Source code
│   └── com/parkingLot/
│       ├── models/           # Core classes (ParkingLot, Floor, Vehicle, etc.)
│       ├── controllers/      # Business logic layer
│       ├── views/            # Swing GUI classes
│       ├── database/         # SQLite connection and queries
│       └── utils/            # Helper classes (ID generation, builders, etc.)
├── target/                    # Compiled output (auto-generated)
└── lib/                       # Legacy JARs (no longer needed)
```

---

## Quick Start

### Prerequisites
- Java 17 or higher installed
- Maven 3.6+ installed

### Build the Application

```bash
# Clean and compile
mvn clean compile

# Create executable JAR with all dependencies
mvn clean package
```

This will create `ParkingLotManagement.jar` in the `target/` directory.

### Run the Application

```bash
# Run directly with Maven
mvn exec:java -Dexec.mainClass="com.parkingLot.views.MainFrame"

# Or run the compiled JAR
java -jar target/ParkingLotManagement.jar
```

---

## Design Patterns

- **Composite Pattern**: Used for parking lot structure hierarchy (ParkingLot → Floor → ParkingSpot)
- **Strategy Pattern**: Used for fine calculation schemes (Fixed, Hourly, Progressive)

---

## Development

### Package Structure

All packages use the base: `com.parkingLot`

- `models/`: Core domain classes
  - `vehicles/`: Vehicle types (Car, Motorcycle, SUV, etc.)
  - `spots/`: Spot-related classes and enums
  - `fines/`: Fine calculation strategies
  - `components/`: Composite pattern components
- `controllers/`: Business logic controllers
- `views/`: Swing GUI panels and frames
- `database/`: SQLite connection and schema
- `utils/`: Utility classes

### Database

- **Connection String**: `jdbc:sqlite:src/com/parkingLot/database/parking_lot.db`
- Database is automatically initialized on first run

---

## Maven Commands Reference

```bash
# Clean build directory
mvn clean

# Compile source code
mvn compile

# Package into executable JAR
mvn package

# Clean and package
mvn clean package

# Run application
mvn exec:java -Dexec.mainClass="com.parkingLot.views.MainFrame"
```

---

## Notes

- All dependencies are automatically managed by Maven
- The `target/` directory is ignored by git
- Maven dependencies are cached in `~/.m2/repository/`
- The application requires Java 17 or higher to run

