USE cartappdb;

-- Disable FK checks
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables - order doesn’t matter
DROP TABLE IF EXISTS attachments;
DROP TABLE IF EXISTS advertisements;
DROP TABLE IF EXISTS advertisements_attachments;
DROP TABLE IF EXISTS capabilities;
DROP TABLE IF EXISTS roles_capabilities;
DROP TABLE IF EXISTS fuel_types;
DROP TABLE IF EXISTS vehicle_types;
DROP TABLE IF EXISTS contact_info;
DROP TABLE IF EXISTS location_coordinates;
DROP TABLE IF EXISTS location_dummy;
DROP TABLE IF EXISTS vehicle_details;
DROP TABLE IF EXISTS vehicle_type;
DROP TABLE IF EXISTS engine_specifications;
DROP TABLE IF EXISTS makers;
DROP TABLE IF EXISTS models;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS makers_bike;
DROP TABLE IF EXISTS makers_car;
DROP TABLE IF EXISTS makers_truck;
DROP TABLE IF EXISTS models_bike;
DROP TABLE IF EXISTS models_car;
DROP TABLE IF EXISTS models_truck;

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;

SET session_replication_role = 'replica';


DROP TABLE IF EXISTS
cart.advertisements,
cart.attachments,
cart.capabilities,
cart.contact_info,
cart.engine_specifications,
cart.fuel_types,
cart.makers_bike,
cart.makers_car,
cart.makers_truck,
cart.models_bike,
cart.models_car,
cart.models_truck,
cart.roles,
cart.roles_capabilities,
cart.users,
cart.vehicle_details,
cart.vehicle_types
CASCADE;


SET session_replication_role = 'origin';
