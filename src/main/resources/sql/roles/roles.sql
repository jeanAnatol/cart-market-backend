USE cartappdb;

INSERT INTO cartappdb.roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'MODERATOR'),
(3, 'USER');

ALTER TABLE cartappdb.roles AUTO_INCREMENT = 4;