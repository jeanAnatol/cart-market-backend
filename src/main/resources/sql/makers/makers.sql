USE cartappdb;

INSERT INTO cartappdb.makers_bike (id, name) VALUES
(1, 'APRILIA'),
(2, 'BENELLI'),
(3, 'BMW'),
(4, 'DUCATI'),
(5, 'HARLEY-DAVIDSON'),
(6, 'HONDA'),
(7, 'HUSQVARNA'),
(8, 'INDIAN'),
(9, 'KAWASAKI'),
(10, 'KTM'),
(11, 'SUZUKI'),
(12, 'TRIUMPH'),
(13, 'YAMAHA');

ALTER TABLE makers_bike AUTO_INCREMENT = 14;

INSERT INTO cartappdb.makers_car (id, name) VALUES

(1, 'ALFA ROMEO'),
(2, 'AUDI'),
(3, 'BMW'),
(4, 'CITROEN'),
(5, 'DACIA'),
(6, 'FIAT'),
(7, 'FORD'),
(8, 'HONDA'),
(9, 'HYUNDAI'),
(10, 'JEEP'),
(11, 'KIA'),
(12, 'MERCEDES'),
(13, 'NISSAN'),
(14, 'OPEL'),
(15, 'PEUGEOT'),
(16, 'RENAULT'),
(17, 'SKODA'),
(18, 'SUZUKI'),
(19, 'TESLA'),
(20, 'TOYOTA'),
(21, 'VOLKSWAGEN'),
(22, 'LANCIA'),
(23, 'CHEVROLET/DAEWOO');

ALTER TABLE makers_car AUTO_INCREMENT = 24;

INSERT INTO cartappdb.makers_truck (id, name) VALUES
(1, 'DAF'),
(2, 'FORD'),
(3, 'IVECO'),
(4, 'MAN'),
(5, 'MERCEDES-BENZ'),
(6, 'RENAULT'),
(7, 'SCANIA'),
(8, 'VOLVO');

ALTER TABLE makers_truck AUTO_INCREMENT = 9;