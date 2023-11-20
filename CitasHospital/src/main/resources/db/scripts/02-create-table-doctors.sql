CREATE TABLE doctors(
    name VARCHAR(200) NOT NULL,
    dni CHAR(9) PRIMARY KEY,
    num_coleg INT NOT NULL,
    experience INT NOT NULL,
    start_time TIME,
    end_time TIME

);