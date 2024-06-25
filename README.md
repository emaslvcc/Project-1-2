### README.md

---

## Public Transport Routing Application for Maastricht

This application utilizes the General Transit Feed Specification (GTFS) dataset for The Netherlands to provide efficient public transport routing options in Maastricht, focusing solely on bus services. It shows the bus info and calculates the distance and time between two postal codes using direct and tranfer bus connections.

### Installation

1. Ensure you have Java installed (Java 17+ recommended).
2. Compile the application using your favorite IDE or the Java command line compiler.

### Database Setup

1. Import the GTFS data into a relational database using the provided SQL scripts found in `sql/load_gtfs.sql`.
2. Configure your database with appropriate indexes to optimize the performance of query operations.
3. Calculate the accessibility scores by navigating to the `src` directory and running:
   ```bash
   java Calculators/Accessiblity.java
   ```

### Usage

1. Start the application by navigating to the `src` directory and running:
   ```bash
   java GUI/mapFrame.java
   ```
2. To view the Accessibility scores, click on the check mark labeled 'Show Accessiblity'
3. To view a route, enter the start and destination postal codes into the GUI.
4. Select the mode of transportation: `Walk`, `Bike`, `Bus`, or `Bus v2`.
5. Click 'Calculate' to get the route information.


### Features

- **Efficient Route Calculation**: Calculates the shortest route using bus connections. If no bus route exists, the application will inform the user accordingly.
- **Interactive Map Visualization**: Displays bus routes on a map, highlighting the recommended route based on the user's input. Additionally, displays a heat map that shows the accessiblity scores of all the postal codes in Maastricht. 
- **Optimized Database**: Features a well-structured relational database optimized with indexes for quick query responses.

### Running Tests

- Run JUnit test cases located in the `test` directory to ensure application functionality:

### Requirements

- All previous requirements must be applied.
- The case where a postal code does not have access to a bus route must be handled.
- If the user must wait between a bus transfer, that time must be considered a part of the travel time.
- The accessibility measure for all postal codes in Maastricht must be efficiently calculated.

### Troubleshooting

- If the GTFS data fails to load correctly, verify the SQL scripts and the database integrity.
- For GUI issues, ensure all Java Swing components are properly initialized in `GUI/mapFrame.java`.

### Support

For additional help or to report issues, please contact support at [carreyhzz@gmail.com].
