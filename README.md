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

The project is divided into two parts: an **Online Version** (Web-based) and an **Offline Version** (Desktop GUI).

### 1. Online Version (Web)
Runs a local web server. You can access the dashboard via your browser.
- **Double-click** `RunOnline.bat`
- Open browser to: [http://localhost:8080](http://localhost:8080)

### 2. Offline Version (Desktop)
Runs a standalone Java Swing application.
- **Double-click** `RunOffline.bat`

### Test Fleet Data

Need some demo vehicles quickly?

- **Online dashboard:** open *Settings → Load Test Fleet → Load Sample Vehicles* to call the `/api/testdata` endpoint and seed the backend.
- **Offline GUI:** click **Load Test Data** on the toolbar (next to **Import CSV**) to populate the same sample vehicles locally.

Both paths use the `TestFleet` dataset (Car/Bike/Truck) so you can immediately explore analytics, rental flows, and revenue calculations without manually entering vehicles.

## Project Structure

- `Backend/Common` - Shared business logic and models.
- `Backend/Web` - Web server implementation.
- `Backend/Desktop` - Swing GUI implementation.
- `docs/` - Frontend files (HTML/CSS/JS).
- `fleet.txt` - Shared database file.
- `revenue.txt` - Persists cumulative total revenue across sessions.

## Author

Created by Rohit Kumar
