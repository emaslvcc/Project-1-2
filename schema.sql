USE gtfs;

-- Enable local infile for data loading
SET GLOBAL local_infile=1;


DROP TABLE IF EXISTS agency;

CREATE TABLE agency (
  agency_id VARCHAR(100) PRIMARY KEY,
  agency_name VARCHAR(100) ,
  agency_url VARCHAR(100) ,
  agency_timezone VARCHAR(100) ,
  agency_phone VARCHAR(100) 
);
LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/agency.txt' INTO TABLE agency FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;


DROP TABLE IF EXISTS calendar_dates;

CREATE TABLE calendar_dates (
  service_id INT,
  date DATE,
  exception_type TINYINT,
  PRIMARY KEY(service_id,date)
);



LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/calendar_dates.txt' INTO TABLE calendar_dates FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;
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
LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/feed_info.txt' INTO TABLE feed_info FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

 


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



LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/routes.txt' INTO TABLE routes FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;





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



LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/shapes.txt' INTO TABLE shapes FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;


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

LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/stops.txt' INTO TABLE stops FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

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



LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/trips.txt' INTO TABLE trips FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

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


LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/stop_times.txt' INTO TABLE stop_times FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;





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


LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/gtfs/transfers.txt' INTO TABLE transfers FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;



-- below is for phase 3
DROP TABLE IF EXISTS post_codes;

CREATE TABLE post_codes(
	zipcode CHAR(6) PRIMARY KEY,
	latitude DECIMAL(11,7),
	longitude DECIMAL(11,7)
);

LOAD DATA LOCAL INFILE  '/Users/joeld/Downloads/project_1-2_18/DatabaseFiles/MassZipLatLon.csv' INTO TABLE post_codes FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' IGNORE 1 LINES;

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
  `route_id_1` varchar(100) NOT NULL,
  `route_short_name_1` varchar(100) DEFAULT NULL,
  `stop_1_id` varchar(100),
  `route_id_2` varchar(100) NOT NULL,
  `route_short_name_2` varchar(100) DEFAULT NULL,
    `stop_2_id` varchar(100),
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


-- Drop existing table if exists and create the postal codes table
DROP TABLE IF EXISTS post_codes;

CREATE TABLE post_codes (
    zipcode CHAR(6) PRIMARY KEY,
    latitude DECIMAL(11, 7),
    longitude DECIMAL(11, 7)
);

-- Load data into post_codes table
LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/project_1-2_18/DatabaseFiles/MassZipLatLon.csv' 
INTO TABLE post_codes 
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
IGNORE 1 LINES;

-- Drop existing table if exists and create the tourism table
DROP TABLE IF EXISTS tourism;

CREATE TABLE tourism (
    id BIGINT PRIMARY KEY,
    name VARCHAR(228) DEFAULT NULL,
    amenity VARCHAR(100),
    longitude DECIMAL(11, 7),
    latitude DECIMAL(11, 7)
);

-- Load data into tourism table
LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/project_1-2_18/DatabaseFiles/tourism1.csv' 
INTO TABLE tourism 
FIELDS TERMINATED BY ';' 
IGNORE 2 LINES;

-- Drop existing table if exists and create the tourism_accessibility table
DROP TABLE IF EXISTS tourism_accessibility;

CREATE TABLE tourism_accessibility (
    zipcode CHAR(6) PRIMARY KEY,
    tourism INT
);

-- Insert postal codes into tourism_accessibility
INSERT INTO tourism_accessibility (zipcode)
SELECT zipcode
FROM post_codes;

-- Drop existing table if exists and create the shops table
DROP TABLE IF EXISTS shops;

CREATE TABLE shops (
    id BIGINT PRIMARY KEY,
    name VARCHAR(228),
    brand VARCHAR(200),
    amenity VARCHAR(150),
    longitude DECIMAL(11, 7),
    latitude DECIMAL(11, 7)
);

-- Load data into shops table
LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/project_1-2_18/DatabaseFiles/shop1.csv' 
INTO TABLE shops 
FIELDS TERMINATED BY ';' 
OPTIONALLY ENCLOSED BY '"' 
IGNORE 2 LINES;

-- Drop existing table if exists and create the shops_accessibility table
DROP TABLE IF EXISTS shops_accessibility;

CREATE TABLE shops_accessibility (
    zipcode CHAR(6) PRIMARY KEY,
    supermarket DECIMAL(11, 5),
    personal_care DECIMAL(11, 5),
    speciality_store DECIMAL(11, 5),
    service DECIMAL(11, 5)
);

-- Insert postal codes into shops_accessibility
INSERT INTO shops_accessibility (zipcode)
SELECT zipcode
FROM post_codes;

-- Drop existing table if exists and create the amenities table
DROP TABLE IF EXISTS amenities;

CREATE TABLE amenities (
    id BIGINT PRIMARY KEY,
    amenity VARCHAR(228),
    longitude DECIMAL(11, 7),
    latitude DECIMAL(11, 7)
);

-- Load data into amenities table
LOAD DATA LOCAL INFILE '/Users/joeld/Downloads/project_1-2_18/DatabaseFiles/amenities12.csv' 
INTO TABLE amenities 
FIELDS TERMINATED BY ';' 
OPTIONALLY ENCLOSED BY '"' 
IGNORE 2 LINES;

-- Drop existing table if exists and create the amenities_accessibility table
DROP TABLE IF EXISTS amenities_accessibility;

CREATE TABLE amenities_accessibility (
    zipcode CHAR(6) PRIMARY KEY,
    Utilities DECIMAL(11, 5),
    Transportation DECIMAL(11, 5), 
    Recreation_and_Entertainment DECIMAL(11, 5), 
    Public_Services DECIMAL(11, 5), 
    Financial_Services DECIMAL(11, 5),
    Miscellaneous DECIMAL(11, 5),
    Public_Transport DECIMAL(11, 5) DEFAULT NULL
);

-- Insert postal codes into amenities_accessibility
INSERT INTO amenities_accessibility (zipcode)
SELECT zipcode
FROM post_codes;

-- Add foreign key constraints
ALTER TABLE shops_accessibility
ADD CONSTRAINT fk_shops_postcodes
FOREIGN KEY (zipcode)
REFERENCES post_codes(zipcode)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE amenities_accessibility
ADD CONSTRAINT fk_amenities_postcodes
FOREIGN KEY (zipcode)
REFERENCES post_codes(zipcode)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE tourism_accessibility
ADD CONSTRAINT fk_tourism_postcodes
FOREIGN KEY (zipcode)
REFERENCES post_codes(zipcode)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- Add category columns to shops, amenities, and tourism tables
ALTER TABLE shops 
ADD COLUMN category VARCHAR(255) DEFAULT NULL;

ALTER TABLE amenities  
ADD COLUMN category VARCHAR(255) DEFAULT NULL;

ALTER TABLE tourism  
ADD COLUMN category VARCHAR(255) DEFAULT 'tourism';

-- Update category fields for shops
UPDATE shops
SET category = 'supermarket'
WHERE amenity IN ('Supermarket', 'Convenience', 'Bakery', 'Butcher', 'Greengrocer', 'Wine', 
                  'Deli', 'Ice_cream', 'Confectionery', 'Pastry', 'Soft_drugs', 'Tobacco', 
                  'Market');

UPDATE shops
SET category = 'personal_care'
WHERE amenity IN ('Chemist', 'Optician', 'Hearing_aids', 'Pharmacy', 'Medical_supply', 
                  'Nutrition_supplements', 'Hairdresser', 'Beauty', 'Massage', 
                  'Hairdresser_supply', 'Cosmetics', 'Perfumery', 'Clothes', 'Shoes', 
                  'Bag', 'Jewelry', 'Accessories', 'Watches');

UPDATE shops
SET category = 'speciality_store'
WHERE amenity IN ('Furniture', 'Curtain', 'Kitchen', 'Garden_centre', 'Houseware',
                  'Interior_decoration', 'Carpet', 'Bed', 'Electronics', 'Music',
                  'Computer', 'HiFi', 'Mobile_phone', 'Sports', 'Bicycle', 'Outdoor',
                  'Toys', 'Books', 'Party', 'Craft', 'Doityourself', 'Paint', 'Mall',
                  'Department_store', 'Chocolate', 'Antiques', 'Stationery', 'Photo',
                  'Gift', 'Florist', 'Art', 'car', 'newsagent', 'pet', 'baby_goods', 'fabric');

UPDATE shops
SET category = 'service'
WHERE amenity IN ('Dry_cleaning', 'Laundry', 'Storage_rental', 'Ticket', 'Pawnbroker',
                  'Car_repair', 'Car_parts', 'Second_hand', 'Wholesale', 'Coffee', 'Tea',
                  'Beverages', 'Variety_store', 'Kiosk', 'Charity', 'Erotic', 'Cannabis',
                  'Drugs_paraphernalia', 'Tobacco');

-- Update category fields for amenities
UPDATE amenities
SET category = 'Public_Services'
WHERE amenity IN ('police', 'courthouse', 'fire_station', 'hospital', 'clinic', 'pharmacy',
                  'veterinary', 'shelter', 'childcare', 'dentist', 'nursing_home', 'doctors',
                  'prep_school', 'college', 'social_facility', 'townhall', 'school', 'community_centre');

UPDATE amenities
SET category = 'Transportation'
WHERE amenity IN ('post_box', 'parking_entrance', 'parking', 'bicycle_parking', 'moped_parking',
                  'car_wash', 'car_rental', 'taxi', 'charging_station', 'bicycle_rental',
                  'parking_space');

UPDATE amenities
SET category = 'Financial_Services'
WHERE amenity IN ('bank', 'atm', 'bureau_de_change');

UPDATE amenities
SET category = 'Recreation_and_Entertainment'
WHERE amenity IN ('library', 'place_of_worship', 'fountain', 'university', 'resthouse', 'park',
                  'cinema', 'restaurant', 'fast_food', 'pub', 'cafe', 'bar', 'ice_cream',
                  'arts_centre', 'theatre', 'nightclub', 'casino');

UPDATE amenities
SET category = 'Utilities'
WHERE amenity IN ('recycling', 'fuel', 'drinking_water', 'water_point', 'sanitary_dump_station',
                  'toilets', 'shower');

UPDATE amenities
SET category = 'Miscellaneous'
WHERE amenity IN ('post_office', 'information', 'waste_basket', 'clock', 'bench',
                  'vending_machine', 'public_bookcase', 'binoculars', 'luggage_locker',
                  'marketplace', 'photo_booth', 'food_court', 'hunting_stand', 'brothel');

-- Create and populate the amenity_weights table
CREATE TABLE amenity_weights (
    category VARCHAR(100),
    weight DECIMAL(3, 2),
    PRIMARY KEY (category)
);

INSERT INTO amenity_weights (category, weight)
VALUES
    ('Utilities', 0.04),
    ('Transportation', 0.10),
    ('Recreation_and_Entertainment', 0.08),
    ('Public_Services', 0.09),
    ('Financial_Services', 0.07),
    ('Miscellaneous', 0.04),
    ('Public_Transport', 0.17),
    ('Supermarket', 0.10),
    ('Personal_Care', 0.07),
    ('Speciality_Store', 0.06),
    ('Service', 0.05),
    ('Tourism', 0.03);

-- Create the weighted_accessibility_scores view
CREATE OR REPLACE VIEW weighted_accessibility_scores AS
SELECT 
    aa.zipcode,
    (
        COALESCE(sa.supermarket, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Supermarket'), 0) +
        COALESCE(sa.personal_care, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Personal_Care'), 0) +
        COALESCE(sa.speciality_store, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Speciality_Store'), 0) +
        COALESCE(sa.service, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Service'), 0) +
        COALESCE(aa.Public_Services, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Public_Services'), 0) +
        COALESCE(aa.Financial_Services, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Financial_Services'), 0) +
        COALESCE(aa.Miscellaneous, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Miscellaneous'), 0) +
        COALESCE(aa.Transportation, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Transportation'), 0) +
        COALESCE(aa.Recreation_and_Entertainment, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Recreation_and_Entertainment'), 0) +
        COALESCE(aa.Utilities, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Utilities'), 0) +
        COALESCE(aa.Public_Transport, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Public_Transport'), 0) +
        COALESCE(t.tourism, 0) * COALESCE((SELECT weight FROM amenity_weights WHERE category = 'Tourism'), 0)
    ) AS total_weighted_score
FROM 
    amenities_accessibility aa
    LEFT JOIN shops_accessibility sa ON aa.zipcode = sa.zipcode
    LEFT JOIN tourism_accessibility t ON aa.zipcode = t.zipcode;

-- Create a trigger to automatically insert into accessibility tables after a new postcode is added
DELIMITER //

CREATE TRIGGER after_insert_postcode
AFTER INSERT ON post_codes
FOR EACH ROW
BEGIN
    -- Insert into shops_accessibility
    INSERT INTO shops_accessibility (zipcode, supermarket, personal_care, speciality_store, service)
    VALUES (NEW.zipcode, NULL, NULL, NULL, NULL);
    
    -- Insert into amenities_accessibility
    INSERT INTO amenities_accessibility (zipcode, Utilities, Transportation, Recreation_and_Entertainment, Public_Services, Financial_Services, Miscellaneous, Public_Transport)
    VALUES (NEW.zipcode, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
    
    -- Insert into tourism_accessibility
    INSERT INTO tourism_accessibility (zipcode, tourism)
    VALUES (NEW.zipcode, NULL);
END //

DELIMITER ;










