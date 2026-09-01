-- =========================================================
-- Dental Clinic System - Database Schema
-- Matches: Class diagram (User/Admin/Receptionist/Doctor,
-- Patient, Appointment, Bill, TreatmentType)
-- =========================================================

CREATE DATABASE IF NOT EXISTS dental_clinic
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dental_clinic;

-- ---------------------------------------------------------
-- USERS
-- Covers Admin / Receptionist / Doctor via the `role` column,
-- matching the User superclass + subclasses in the class diagram.
-- ---------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- stores the BCrypt hash
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    role          ENUM('ADMIN', 'RECEPTIONIST', 'DOCTOR') NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- PATIENTS
-- ---------------------------------------------------------
CREATE TABLE patients (
    patient_id     INT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    contact_number VARCHAR(20)  NOT NULL,
    address        VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- TREATMENT TYPES
-- ---------------------------------------------------------
CREATE TABLE treatment_types (
    treatment_type_id INT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(100) NOT NULL,
    cost               DECIMAL(10,2) NOT NULL
);

-- ---------------------------------------------------------
-- APPOINTMENTS
-- "For" a Patient (1..0..*), "Attended by" a Doctor (1..0..*),
-- "of type" a TreatmentType (1..0..*), "Has" a Bill (1..0..1)
-- appointment_datetime is needed for the conflict check
-- (existsConflict(doctor, dateTime)) from the sequence diagram.
-- ---------------------------------------------------------
CREATE TABLE appointments (
    appointment_id      INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number   INT NOT NULL,
    status                ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    appointment_datetime  DATETIME NOT NULL,
    patient_id            INT NOT NULL,
    doctor_id              INT NOT NULL,
    treatment_type_id      INT NOT NULL,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appt_patient
        FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appt_doctor
        FOREIGN KEY (doctor_id) REFERENCES users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appt_treatment
        FOREIGN KEY (treatment_type_id) REFERENCES treatment_types(treatment_type_id)
        ON DELETE RESTRICT,

    INDEX idx_doctor_datetime (doctor_id, appointment_datetime)
);

-- ---------------------------------------------------------
-- BILLS
-- 1-to-0..1 with Appointment, so appointment_id is UNIQUE
-- (each appointment can have at most one bill).
-- ---------------------------------------------------------
CREATE TABLE bills (
    bill_id        INT AUTO_INCREMENT PRIMARY KEY,
    amount         DECIMAL(10,2) NOT NULL,
    appointment_id INT NOT NULL UNIQUE,
    issued_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bill_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
        ON DELETE RESTRICT
);
