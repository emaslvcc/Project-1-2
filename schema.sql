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
CREATE TABLE transfers (
  from_stop_id VARCHAR(40),
  to_stop_id VARCHAR(40),
  from_route_id VARCHAR(255),
  to_route_id VARCHAR(255),
  from_trip_id INT,
  to_trip_id INT,
  transfer_type INT,
  PRIMARY KEY (from_stop_id, from_trip_id, to_trip_id),
  FOREIGN KEY (from_stop_id) REFERENCES stops(stop_id),
  FOREIGN KEY (to_stop_id) REFERENCES stops(stop_id),
  FOREIGN KEY (from_trip_id) REFERENCES trips(trip_id),
  FOREIGN KEY (to_trip_id) REFERENCES trips(trip_id)
);

LOAD DATA LOCAL INFILE '/Users/Carrey/Desktop/UM/Year\ 1/Project/Project\ 1-2/phase2/gtfs/transfers.txt' INTO TABLE transfers FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;







