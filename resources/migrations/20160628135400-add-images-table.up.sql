DROP TABLE IF EXISTS images;
--;;
CREATE TABLE images
    (uri VARCHAR(15) PRIMARY KEY,
    user VARCHAR(30) NOT NULL,
    altered VARCHAR(50),
    original VARCHAR(50),
    timestamp TIMESTAMP);