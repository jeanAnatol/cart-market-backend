INSERT INTO cartappdb.capabilities (id, description, name) VALUES

(1, "The capability to create new ads and users.", "CREATE"),
(2, "The capability to read advertisements.", "READ"),
(3, "The capability to update fields of user or advertisement.", "UPDATE"),
(4, "The capability to delete users or advertisements.", "DELETE");

ALTER TABLE cartappdb.capabilities AUTO_INCREMENT = 5;