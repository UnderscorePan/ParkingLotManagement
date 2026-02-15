# Parking Lot Management System

CCP6224 - Object-Oriented Analysis and Design

---

## Group Members

- Hew Wee Bo
- Shanjif Cakravathi
- Merey Abilkhan
- Liaw Yong Loon

---

### Base Package

All packages use the base: `com.parkingLot`

### Required Packages

Create these packages in your IntelliJ project (right-click `src` → New → Package):

```
com.parkingLot.models
com.parkingLot.models.tests
com.parkingLot.models.vehicles
com.parkingLot.models.spots
com.parkingLot.models.fines
com.parkingLot.controllers
com.parkingLot.views
com.parkingLot.database
com.parkingLot.utils

```

---

## Packages

- `models/`: Your core classes (ParkingLot, Floor, Vehicle, etc.)
- `vehicles/`: Vehicle-related classes (Car, Motorcycle, SUV, etc.)
- `spots/`: Spot-related classes
- `fines/`: Fine calculation strategies
- `controllers/`: Business logic between GUI and models
- `views/`: Swing GUI classes
- `database/`: SQLite connection and queries
- `utils/`: Helper classes (date calculations, ID generation, etc.)
- `tests/`: For testing all modules without GUI

---

## Design Pattern

- **Composite Pattern**: Used for parking lot structure hierarchy

---

## Plugins and libraries to use

(These are from IntelliJ IDEA Community Edition)

- Database Navigator
- PlantUML4IDEA (requires graphviz to render)
- SQLite JDBC driver (needed for SQLite)
- SwingUI Designer (Important)
- SimpleSqliteBrowser (Important)

## Notes

- String url = "jdbc:sqlite:database/parking_lot.db"; // connection string

## Features

- [ ] Multi-level parking lot structure
- [ ] Vehicle entry/exit management
- [ ] Fine calculation (3 schemes)
- [ ] Payment processing
- [ ] Admin and reporting panels


## Maven Quick Start Guide

## Prerequisites
- Java 17 or higher
- Maven 3.6+ installed

## Common Maven Commands

### 1. Clean and Compile
```bash
mvn clean compile
```
Removes old build files and compiles all source code.

### 2. Run Tests
```bash
# Run the fine system test
mvn exec:java -Dexec.mainClass="com.parkingLot.tests.FineTester"

# Run vehicle test
mvn exec:java -Dexec.mainClass="com.parkingLot.tests.VehicleTester"

# Run main application
mvn exec:java -Dexec.mainClass="com.parkingLot.Main"
```

### 3. Package JAR
```bash
mvn clean package
```
Creates executable JAR in `target/` directory.

### 4. Clean Build Directory
```bash
mvn clean
```
### 5. Run
```
java -jar target/ParkingLotManagement-1.0-SNAPSHOT-jar-with-dependencies.jar
```
## Project Structure
```
ParkingLotManagement/
├── pom.xml                    # Maven configuration
├── src/                       # Source code
│   └── com/parkingLot/
│       ├── Main.java
│       ├── models/
│       ├── controllers/
│       ├── views/
│       ├── database/
│       ├── utils/
│       └── tests/
├── target/                    # Compiled output (auto-generated)
└── lib/                       # Old manual JAR (can be removed)
```

## Dependencies
All dependencies are automatically downloaded by Maven:
- **SQLite JDBC 3.47.1.0** - Database driver

## First Time Setup
1. Open terminal in project directory
2. Run: `mvn clean compile`
3. Maven will download all dependencies
4. Ready to develop!

## IntelliJ IDEA Integration
- IntelliJ automatically detects `pom.xml`
- Click "Import" when prompted
- All dependencies will be configured automatically
- Use "Maven" tab on right side for commands

## Notes
- The `lib/` folder with manual JARs is no longer needed
- Maven stores dependencies in `~/.m2/repository/`
- `target/` folder is ignored by git (build output)
