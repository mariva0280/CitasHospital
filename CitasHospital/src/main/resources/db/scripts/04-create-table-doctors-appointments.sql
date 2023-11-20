CREATE TABLE doctors_appointments (
    id CHAR(36) PRIMARY KEY,
    dni_patients CHAR(9) NOT NULL,
    dni_doctors CHAR(9) NOT NULL,
    days DATE NOT NULL,
    hours TIME NOT NULL,
    FOREIGN KEY (dni_patients) REFERENCES patients(dni),
    FOREIGN KEY (dni_doctors) REFERENCES doctors(dni)
);