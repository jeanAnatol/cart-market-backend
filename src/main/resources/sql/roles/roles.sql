USE cartappdb;

INSERT INTO cartappdb.roles (id, name) VALUES
(1, "ADMIN"),
(2, "MODERATOR"),
(3, "USER");

ALTER TABLE cartappdb.roles AUTO_INCREMENT = 4;

--FOR POSTGRES

INSERT INTO cart.roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'MODERATOR'),
(3, 'USER');

SELECT setval('cart.roles_id_seq', 3, true);