ALTER TABLE location_coordinates MODIFY coordinates POINT SRID 4326;

--POSTGRES
CREATE EXTENSION postgis;
