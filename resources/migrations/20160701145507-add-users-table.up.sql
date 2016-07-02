DROP TABLE IF EXISTS users;
--;;
CREATE TABLE users
    (name VARCHAR(30) COLLATE nocase PRIMARY KEY,
    email VARCHAR(40),
    password VARCHAR(50) NOT NULL);