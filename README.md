### README.md

---

## Public Transport Routing Application for Maastricht

This application utilizes the General Transit Feed Specification (GTFS) dataset for The Netherlands to provide efficient public transport routing options in Maastricht, focusing solely on bus services. It calculates the distance and time between two postal codes using direct bus connections.

### Installation

1. Ensure you have Java installed (Java 17+ recommended).
2. Compile the application using your favorite IDE or the Java command line compiler.

### Database Setup

1. Import the GTFS data into a relational database using the provided SQL scripts found in `sql/load_gtfs.sql`.
2. Configure your database with appropriate indexes to optimize the performance of query operations.

### Usage

1. Start the application by navigating to the `src` directory and running:
   ```bash
   java GUI/mapFrame.java
   ```
2. Enter the start and destination postal codes into the GUI.
3. Select the mode of transportation: `walk`, `bike`, or `bus`.
4. Click 'Calculate' to get the route information.

### Features

- **Efficient Route Calculation**: Calculates the shortest direct route using bus connections. If no direct bus route exists, the application will inform the user accordingly.
- **Interactive Map Visualization**: Displays bus routes on a map, highlighting the recommended route based on the user's input.
- **Optimized Database**: Features a well-structured relational database optimized with indexes for quick query responses.

### Running Tests

- Run JUnit test cases located in the `test` directory to ensure application functionality:

### Requirements

- GTFS data must be stored in a relational database.
- SQL queries should be optimized for fast performance.
- The GUI must display all bus routes and indicate which bus route the user should take based on their input.

### Troubleshooting

- If the GTFS data fails to load correctly, verify the SQL scripts and the database integrity.
- For GUI issues, ensure all Java Swing components are properly initialized in `GUI/mapFrame.java`.

### Support

For additional help or to report issues, please contact support at [carreyhzz@gmail.com].
