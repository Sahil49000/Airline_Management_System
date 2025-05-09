CREATE DATABASE IF NOT EXISTS airlinemanagement_updated;
USE airlinemanagement_updated;

CREATE TABLE IF NOT EXISTS passenger (
    passenger_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    aadhar VARCHAR(12) NOT NULL UNIQUE,
    nationality VARCHAR(100),
    address VARCHAR(255),
    gender VARCHAR(10),
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(255) UNIQUE,
    date_of_birth DATE
);

CREATE TABLE IF NOT EXISTS airport (
    airport_id INT AUTO_INCREMENT PRIMARY KEY,
    airport_code VARCHAR(3) NOT NULL,
    airport_name VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    country VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS flight (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(50) NOT NULL,
    flight_name VARCHAR(100) NOT NULL,
    departure_airport_id INT,
    arrival_airport_id INT,
    travel_date DATE NOT NULL,
    available_seats INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    journey_time VARCHAR(50) NOT NULL,
    arrival_date DATE,
    departure_date DATE,
    FOREIGN KEY (departure_airport_id) REFERENCES airport(airport_id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airport(airport_id)
);
 
CREATE TABLE IF NOT EXISTS booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    passenger_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pnr_number VARCHAR(20) NOT NULL UNIQUE,
    seat_number VARCHAR(10),
    booking_status VARCHAR(20) DEFAULT 'Confirmed',
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id),
    FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id)
);

CREATE TABLE IF NOT EXISTS login (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS aircraft (
    aircraft_id INT AUTO_INCREMENT PRIMARY KEY,
    aircraft_model VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    manufacturer VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS flight_aircraft (
    flight_id INT,
    aircraft_id INT,
    PRIMARY KEY (flight_id, aircraft_id),
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id),
    FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id)
);

CREATE TABLE IF NOT EXISTS employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    employee_number VARCHAR(20) UNIQUE,
    department VARCHAR(100),
    job_title VARCHAR(100),
    hire_date DATE,
    airport_id INT,
    FOREIGN KEY (airport_id) REFERENCES airport(airport_id)
);

CREATE TABLE IF NOT EXISTS baggage (
    baggage_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    weight DECIMAL(5, 2),
    description VARCHAR(255),
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);

CREATE TABLE IF NOT EXISTS flight_status (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT,
    status VARCHAR(50),
    status_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);

CREATE TABLE IF NOT EXISTS payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10, 2),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);

CREATE TABLE IF NOT EXISTS customer_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_id INT,
    flight_id INT,
    rating INT,
    comment TEXT,
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id),
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);

CREATE TABLE IF NOT EXISTS route (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    departure_airport_id INT,
    arrival_airport_id INT,
    distance DECIMAL(10, 2),
    FOREIGN KEY (departure_airport_id) REFERENCES airport(airport_id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airport(airport_id)
);