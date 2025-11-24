# Vehicle Rental System

A Java-based Vehicle Rental System that allows users to manage a fleet of vehicles (Cars, Bikes, Trucks). This application features a graphical user interface (GUI) for easy interaction.

## Features

- **Fleet Management:** Add new vehicles to the fleet.
- **Rent/Return:** Rent out vehicles and return them, updating their status.
- **Search:** Search for vehicles by ID.
- **Persistence:** Data is saved to a local file (`fleet.txt`) so records are kept between sessions.
- **Import/Export:** Import and export fleet data using CSV format.
- **GUI:** User-friendly Swing interface.

## Requirements

- Java Development Kit (JDK) 8 or higher.

## How to Run

### Option 1: Run from Source

1.  Compile the project:
    ```bash
    javac -cp . VehicleRental/*.java
    ```
2.  Run the GUI:
    ```bash
    java -cp . VehicleRental.FleetGUILauncher
    ```

### Option 2: Run the JAR file

If you have downloaded the `VehicleRental.jar` file:

```bash
java -jar VehicleRental.jar
```

## Project Structure

- `VehicleRental/` - Package containing source code.
- `fleet.txt` - Database file for storing vehicle records.
- `Report.tex` - Project report in LaTeX format.

## Author

Created by Rohit Kumar
