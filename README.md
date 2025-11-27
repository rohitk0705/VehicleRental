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

### Option 3: Run as Web Application

You can run the application as a local web server and access it via your browser.

1.  Run the server:
    ```bash
    java -cp VehicleRental.jar VehicleRental.WebServer
    ```
    (Or simply double-click `run_server.bat`)

2.  Open your browser and visit: [http://localhost:8080](http://localhost:8080)

## Project Structure

- `VehicleRental/` - Package containing source code.
- `docs/` - HTML/CSS/JS files for the web interface.
- `fleet.txt` - Database file for storing vehicle records.
- `Report.tex` - Project report in LaTeX format.

## Author

Created by Rohit Kumar
