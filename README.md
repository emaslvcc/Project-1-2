3.2 Phase 2: Public Transport Routing

For this phase, you have been provided with a GTFS (General Transit Feed Specification4)
dataset for The Netherlands. Calculate the distance and time between two postal codes, now
considering public transport options.
Assume that the only mode of public transport in Maastricht is by bus (ignore the Arriva train
connections between Randwyck, Centraal, and Nord stations).
3.2.1 Tasks:

1. Develop an efficient algorithm to find the closest bus stops, bus routes, and times for
   trips for a given postal code.

2. Store the GTFS data5 in a well-structured relational database and write efficient SQL
   queries.
   Hint: Use indexes where appropriate.

3. Enhance your visualization to accurately depict bus routes.
   You may use the city bus map as inspiration.
4. Write JUnit test cases and report code coverage statistics (aim for very high coverage).

3.2.2 Requirements:

1. Store GTFS data in a relational database. Optimize all SQL queries for performance
   (minimal query wait time).
2. Calculate time by bus. Only direct connections count. If no direct connection exists
   between two postal codes the GUI must indicate this.

3.2.3 To Pass:

1. Have a correctly implemented routing algorithm for finding the shortest direct route.
2. Include a GUI with all bus routes. Upon user input the GUI must indicate in some
   manner which bus route the user should take.

3.2.4 How to run the application?

Enter the start zip code and destination zip code, choose 'walk', 'bike' or 'bus', then press 'Calculate'.
