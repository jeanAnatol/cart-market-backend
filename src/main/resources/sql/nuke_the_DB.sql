USE cartappdb;

-- Disable Foreign Key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables - order doesn’t matter
DROP TABLE IF EXISTS attachments;
DROP TABLE IF EXISTS advertisements;
DROP TABLE IF EXISTS capabilities;
DROP TABLE IF EXISTS roles_capabilities;
DROP TABLE IF EXISTS fuel_types;
DROP TABLE IF EXISTS vehicle_types;
DROP TABLE IF EXISTS make_vehicle_types;
DROP TABLE IF EXISTS contact_info;
DROP TABLE IF EXISTS location_coordinates;
DROP TABLE IF EXISTS location_dummy;
DROP TABLE IF EXISTS vehicle_details;
DROP TABLE IF EXISTS engine_specifications;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS makers;
DROP TABLE IF EXISTS models;

-- Re-enable Foreign Key checks
SET FOREIGN_KEY_CHECKS = 1;