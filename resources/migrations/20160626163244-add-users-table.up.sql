DROP TABLE IF EXISTS users;
--;;
CREATE TABLE users
 (id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(30) NOT NULL,
  email VARCHAR(40),
  password VARCHAR(50) NOT NULL);