# Parking Lot Management System

CCP6224 - Object-Oriented Analysis and Design

---

## Group Members

- Hew Wee Bo
- Shanjif Cakravathi
- tba
- tba

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

---

## Design Pattern

- **Composite Pattern**: Used for parking lot structure hierarchy

---

## Plugins and libraries to use

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