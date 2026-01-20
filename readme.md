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
