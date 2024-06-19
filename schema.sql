USE gtfs;

DROP TABLE IF EXISTS agency;

CREATE TABLE agency (
  agency_id VARCHAR(100) PRIMARY KEY,
  agency_name VARCHAR(100) ,
  agency_url VARCHAR(100) ,
  agency_timezone VARCHAR(100) ,
  agency_phone VARCHAR(100) 
);
LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/agency.txt' INTO TABLE agency FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;



DROP TABLE IF EXISTS calendar_dates;

CREATE TABLE calendar_dates (
  service_id INT,
  date DATE,
  exception_type TINYINT,
  PRIMARY KEY(service_id,date)
);



LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/calendar_dates.txt' INTO TABLE calendar_dates FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;
/*SELECT count(*)
from calendar_dates cd  ;*/

DROP TABLE IF EXISTS feed_info;

CREATE TABLE feed_info (
    feed_publisher_name VARCHAR(255),     
    feed_id CHAR(2),
    feed_publisher_url VARCHAR(255),          
    feed_lang CHAR(2),    
    feed_start_date DATE,           
    feed_end_date DATE,        
    feed_version CHAR(4) 
);
LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/feed_info.txt' INTO TABLE feed_info FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

 


DROP TABLE IF EXISTS routes;

CREATE TABLE routes (
  route_id varchar(255) PRIMARY KEY,
  agency_id VARCHAR(100) ,
  route_short_name VARCHAR(255),
  route_long_name VARCHAR(255) ,
  route_desc VARCHAR(255) ,
  route_type VARCHAR(255) ,
  route_color CHAR(6) ,
  route_text_color VARCHAR(255),
  route_url VARCHAR(255),
  FOREIGN KEY(agency_id) REFERENCES agency(agency_id),
  index idx_route_short_name (route_short_name),
  index idx_route_type (route_type)

);



LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/routes.txt' INTO TABLE routes FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;





SELECT count(*) AS routes_count
from routes;


DROP TABLE IF EXISTS shapes;
CREATE TABLE shapes (
  shape_id VARCHAR(255),
  shape_pt_sequence SMALLINT,
  shape_pt_lat FLOAT,
  shape_pt_lon FLOAT,
  shape_dist_traveled INT,
  PRIMARY KEY(shape_id,shape_pt_sequence)
);
CREATE INDEX idx_shape_pt_sequence ON shapes (shape_pt_sequence);



LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/shapes.txt' INTO TABLE shapes FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;


DROP TABLE IF EXISTS stops;

CREATE TABLE stops (
  stop_id VARCHAR(40) PRIMARY KEY,
   stop_code VARCHAR(255) ,
  stop_name VARCHAR(64) ,
  stop_lat DECIMAL(11,7) ,
  stop_lon DECIMAL(11,7) ,
  location_type VARCHAR(10) ,
  parent_station VARCHAR(255) ,
  stop_timezone VARCHAR(64),
  wheelchair_boarding VARCHAR(100) ,
  platform_code VARCHAR(32) ,
  zone_id VARCHAR(16) 
);

LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/stops.txt' INTO TABLE stops FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

DROP TABLE IF EXISTS trips;

CREATE TABLE trips (
   
  route_id varchar(255),
   service_id INT,
   trip_id INT PRIMARY KEY,
   realtime_trip_id VARCHAR(255),
  trip_headsign VARCHAR(64),
  trip_short_name VARCHAR(64),
  trip_long_name VARCHAR(32),
  direction_id INT,
  block_id varchar(255),
  shape_id VARCHAR(255),
  wheelchair_accessible varchar(255),
  bikes_allowed varchar(255),
  foreign key (route_id) references routes(route_id),

  INDEX `route_id` (route_id),
  INDEX `shape_id` (shape_id)
);



LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/trips.txt' INTO TABLE trips FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

SELECT count(*)
from trips t ;

DROP TABLE IF EXISTS stop_times;

CREATE TABLE stop_times (
  trip_id INT,
  stop_sequence SMALLINT,
  stop_id VARCHAR(40),
  stop_headsign VARCHAR(100),
  arrival_time TIME,
  departure_time TIME,
  pickup_type TINYINT,
  drop_off_type TINYINT,
  timepoint TINYINT, 
  shape_dist_traveled INT DEFAULT 0,
  fare_units_traveled INT DEFAULT 0,
  PRIMARY KEY(trip_id,stop_sequence,stop_id),
  FOREIGN KEY (trip_id) REFERENCES trips(trip_id),
  FOREIGN KEY (stop_id) REFERENCES stops(stop_id),
  INDEX `trip_id` (trip_id),
  INDEX `stop_id` (stop_id)

);
CREATE INDEX idx_stop_sequence ON stop_times (stop_sequence);
CREATE INDEX idx_arrival_time ON stop_times (arrival_time);
CREATE INDEX idx_departure_time ON stop_times (departure_time);
CREATE INDEX idx_trip_idANDstop_sequence on stop_times(trip_id, stop_sequence);
CREATE INDEX idx_stop_idANDtrip_id on stop_times(stop_id, trip_id);


select count(*)
from stop_times st;


LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/stop_times.txt' INTO TABLE stop_times FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;





DROP TABLE IF EXISTS transfers;

CREATE TABLE transfers (
  from_stop_id VARCHAR(40),
  to_stop_id VARCHAR(40),
  from_route_id  VARCHAR(255),
  to_route_id VARCHAR(255) ,
  from_trip_id INT,
  to_trip_id INT,
  transfer_type INT,
  PRIMARY KEY(from_stop_id,from_trip_id, to_trip_id)
 

);


LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/transfers.txt' INTO TABLE transfers FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

DROP TABLE IF EXISTS post_codes;

CREATE TABLE post_codes(
	zipcode CHAR(6) PRIMARY KEY,
	latitude DECIMAL(11,7),
	longitude DECIMAL(11,7)
);

LOAD DATA LOCAL INFILE  '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/MassZipLatLon.csv' INTO TABLE post_codes FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;
INSERT into post_codes values(
'6212xp','50.831516', '5.69584');

               
               


CREATE TABLE post_codes_join_table AS
SELECT 
    pc.zipcode,
    pc.latitude,
    pc.longitude,
    pc2.zipcode AS sec_zipcode,
    pc2.latitude AS sec_lat,
    pc2.longitude AS sec_lon
FROM 
    post_codes pc
JOIN 
    post_codes pc2 
ON 
    pc.zipcode <> pc2.zipcode
where ST_Distance_Sphere(POINT(pc.latitude,
    pc.longitude), POINT(pc2.latitude,pc2.longitude)) > 600
;



CREATE table AllTransferStops (
  `route_id_1` varchar(255) NOT NULL,
  `route_short_name_1` varchar(255) DEFAULT NULL,
  `stop_1_id` varchar(255),
  `route_id_2` varchar(255) NOT NULL,
  `route_short_name_2` varchar(255) DEFAULT NULL,
    `stop_2_id` varchar(255),
  PRIMARY KEY (`route_id_1`,`stop_1_id`,`route_id_2`,`stop_2_id`)
);


TRUNCATE table AllTransferStops ; 
insert into AllTransferStops (           
SELECT
    a.route_id AS route_id_1,
    a.route_short_name AS route_short_name_1,
    a.stop_id AS stop_1,
    b.route_id AS route_id_2,
    b.route_short_name AS route_short_name_2,
    b.stop_id AS stop_2
FROM (
    SELECT DISTINCT t.route_id, r.route_short_name, st.stop_id, s.stop_lat, s.stop_lon
    FROM trips t
    JOIN stop_times st ON t.trip_id = st.trip_id
    JOIN routes r ON t.route_id = r.route_id
    JOIN stops s ON s.stop_id = st.stop_id
    WHERE (s.stop_lat BETWEEN 50.803792 AND 50.9)
      AND (s.stop_lon BETWEEN 5.640811 AND 5.739475)
      AND r.route_type = '3'
      AND r.route_short_name <> '797'
      AND r.route_short_name <> '62'
      AND r.route_short_name NOT LIKE '%trein%'
) a
JOIN (
    SELECT DISTINCT t.route_id, r.route_short_name, st.stop_id, s.stop_lat, s.stop_lon
    FROM trips t
    JOIN stop_times st ON t.trip_id = st.trip_id
    JOIN routes r ON t.route_id = r.route_id
    JOIN stops s ON s.stop_id = st.stop_id
    WHERE (s.stop_lat BETWEEN 50.803792 AND 50.9)
      AND (s.stop_lon BETWEEN 5.640811 AND 5.739475)
      AND r.route_type = '3'
      AND r.route_short_name <> '797'
      AND r.route_short_name <> '62'
      AND r.route_short_name NOT LIKE '%trein%'
) b 
ON (a.stop_id = b.stop_id OR ST_Distance_Sphere(point(a.stop_lon, a.stop_lat), point(b.stop_lon, b.stop_lat)) < 80)
AND a.route_id <> b.route_id
ORDER BY a.route_id, b.route_id);



drop table if exists tempTransfer;
 CREATE TABLE tempTransfer (	
   					first_start_bus_stop_id VARCHAR(40),
   					first_end_bus_stop_id VARCHAR(40),
   					first_route_id VARCHAR(255),
                    first_route_short_name VARCHAR(255),
                    first_trip_id INT,
                    first_departure_time TIME,
                    first_arrival_time TIME,
                    first_trip_time INT,
                    second_start_bus_stop_id VARCHAR(40),
    second_end_bus_stop_id VARCHAR(40),
    second_route_id VARCHAR(255),
    second_route_short_name VARCHAR(255),
    second_trip_id INT,
    second_departure_time TIME,
    second_arrival_time TIME,
    second_trip_time INT,
    distanceToFirstBusstop float,
    timeOfArrDestination TIME,
    timeOfDepart Time
                );
               
   CREATE TABLE `preComputedTripDetails` (
  `start_stop_id` varchar(40) DEFAULT NULL,
  `end_stop_id` varchar(40) DEFAULT NULL,
  `route_id` varchar(255) DEFAULT NULL,
  `route_short_name` varchar(255) DEFAULT NULL,
  `route_long_name` varchar(255) DEFAULT NULL,
  `trip_id` int DEFAULT NULL,
  `start_departure_time` time DEFAULT NULL,
  `end_arrival_time` time DEFAULT NULL,
  `trip_time` int DEFAULT NULL,
  KEY `idx_trip_details_query` (`start_stop_id`,`end_stop_id`,`route_id`,`start_departure_time`)
) ;


                   
               
  INSERT into preComputedTripDetails(                        
               SELECT
    st1.stop_id AS start_stop_id,
    st2.stop_id AS end_stop_id,
    t.route_id,
    r.route_short_name,
    r.route_long_name,
    st1.trip_id,
    st1.departure_time AS start_departure_time,
    st2.arrival_time AS end_arrival_time,
    TIMESTAMPDIFF(MINUTE, st1.departure_time, st2.arrival_time) AS trip_time
FROM
    stop_times st1
JOIN
    stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence
JOIN
    trips t ON t.trip_id = st1.trip_id
JOIN
    routes r ON t.route_id = r.route_id);

-- Index to speed up subsequent queries
CREATE INDEX idx_start_stop_id ON PrecomputedTrips(start_stop_id);
CREATE INDEX idx_end_stop_id ON PrecomputedTrips(end_stop_id);
CREATE INDEX idx_route_id ON PrecomputedTrips(route_id);
CREATE INDEX idx_trip_details ON preComputedTripDetails (start_stop_id, end_stop_id, route_id, start_departure_time);

   
-- test (not need now)

CREATE table distanceToBusstop(
	start_latitude DECIMAL(11,7),
	start_longitude DECIMAL(11,7),
	end_latitude DECIMAL(11,7),
	end_longitude DECIMAL(11,7),
	distance INT,
	primary key(start_latitude,start_longitude,
	end_latitude ,
	end_longitude)
);













