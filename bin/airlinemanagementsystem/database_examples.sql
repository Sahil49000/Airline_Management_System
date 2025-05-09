-- Insert Airports
INSERT INTO airport (airport_code, airport_name, city, country) VALUES
('BOM', 'Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India'),
('DEL', 'Indira Gandhi International Airport', 'Delhi', 'India'),
('BLR', 'Kempegowda International Airport', 'Bangalore', 'India'),
('MAA', 'Chennai International Airport', 'Chennai', 'India'),
('CCU', 'Netaji Subhas Chandra Bose International Airport', 'Kolkata', 'India'),
('HYD', 'Rajiv Gandhi International Airport', 'Hyderabad', 'India'),
('COK', 'Cochin International Airport', 'Kochi', 'India');

-- Insert Aircraft
INSERT INTO aircraft (aircraft_model, capacity, manufacturer) VALUES
('Boeing 737-800', 189, 'Boeing'),
('Airbus A320', 180, 'Airbus'),
('Boeing 777-300ER', 350, 'Boeing'),
('Airbus A321', 220, 'Airbus'),
('Boeing 787-9', 290, 'Boeing');

-- Insert Routes
INSERT INTO route (departure_airport_id, arrival_airport_id, distance) VALUES
(1, 2, 1150.00), -- Mumbai to Delhi
(2, 3, 1740.00), -- Delhi to Bangalore
(3, 4, 350.00),  -- Bangalore to Chennai
(4, 5, 1660.00), -- Chennai to Kolkata
(5, 1, 1660.00), -- Kolkata to Mumbai
(1, 6, 620.00),  -- Mumbai to Hyderabad
(6, 7, 900.00);  -- Hyderabad to Kochi

INSERT INTO flight (flight_number, flight_name, departure_airport_id, arrival_airport_id,
                    travel_date, available_seats, price, journey_time, departure_date, arrival_date) VALUES
-- Mumbai to Delhi flights
('AI-101', 'Air India Express', 1, 2, CURDATE(), 45, 4500.00, '2h 15m', CURDATE(), DATE_ADD(CURDATE(), INTERVAL '2:15' HOUR_MINUTE)),
('6E-201', 'IndiGo', 1, 2, CURDATE(), 30, 4200.00, '2h 10m', CURDATE(), DATE_ADD(CURDATE(), INTERVAL '2:10' HOUR_MINUTE)),
('UK-301', 'Vistara', 1, 2, CURDATE(), 25, 4800.00, '2h 20m', CURDATE(), DATE_ADD(CURDATE(), INTERVAL '2:20' HOUR_MINUTE)),

-- Delhi to Bangalore flights
('AI-401', 'Air India', 2, 3, CURDATE(), 40, 5500.00, '2h 45m', CURDATE(), DATE_ADD(CURDATE(), INTERVAL '2:45' HOUR_MINUTE)),
('SG-501', 'SpiceJet', 2, 3, CURDATE(), 35, 5200.00, '2h 40m', CURDATE(), DATE_ADD(CURDATE(), INTERVAL '2:40' HOUR_MINUTE)),

-- Tomorrow's flights
('AI-102', 'Air India Express', 1, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 50, 4600.00, '2h 15m',
 DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL '2:15' HOUR_MINUTE)),
('6E-202', 'IndiGo', 1, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 45, 4300.00, '2h 10m',
 DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL '2:10' HOUR_MINUTE));
 
-- Insert Sample Passengers
INSERT INTO passenger (name, aadhar, nationality, address, gender, phone, email, date_of_birth) VALUES
('Rahul Sharma', '123456789012', 'Indian', '123 Main St, Mumbai', 'Male', '9876543210', 'rahul@email.com', '1990-05-15'),
('Priya Patel', '234567890123', 'Indian', '456 Park Ave, Delhi', 'Female', '9876543211', 'priya@email.com', '1992-08-20'),
('Amit Kumar', '345678901234', 'Indian', '789 Lake View, Bangalore', 'Male', '9876543212', 'amit@email.com', '1988-11-10'),
('Neha Singh', '456789012345', 'Indian', '321 Garden Rd, Chennai', 'Female', '9876543213', 'neha@email.com', '1995-03-25'),
('Raj Malhotra', '567890123456', 'Indian', '654 Hill St, Kolkata', 'Male', '9876543214', 'raj@email.com', '1991-07-30');

-- Insert Sample Bookings
INSERT INTO booking (flight_id, passenger_id, pnr_number, seat_number, booking_status) VALUES
(1, 1, 'PNR123456', 'A12', 'Confirmed'),
(2, 2, 'PNR234567', 'B15', 'Confirmed'),
(3, 3, 'PNR345678', 'C20', 'Confirmed'),
(4, 4, 'PNR456789', 'D25', 'Confirmed'),
(5, 5, 'PNR567890', 'E30', 'Confirmed');

-- Insert Sample Payments
INSERT INTO payment (booking_id, amount, payment_method, transaction_id) VALUES
(1, 4500.00, 'Credit Card', 'TXN123456'),
(2, 4200.00, 'Debit Card', 'TXN234567'),
(3, 4800.00, 'UPI', 'TXN345678'),
(4, 5500.00, 'Net Banking', 'TXN456789'),
(5, 5200.00, 'Credit Card', 'TXN567890');

-- Insert Sample Employees
INSERT INTO employee (name, employee_number, department, job_title, hire_date, airport_id) VALUES
('Vikram Singh', 'EMP001', 'Operations', 'Flight Operations Manager', '2020-01-15', 1),
('Anita Desai', 'EMP002', 'Customer Service', 'Customer Service Manager', '2019-06-20', 2),
('Rajesh Kumar', 'EMP003', 'Maintenance', 'Aircraft Maintenance Engineer', '2021-03-10', 3),
('Meera Patel', 'EMP004', 'Security', 'Security Supervisor', '2020-08-05', 4),
('Suresh Reddy', 'EMP005', 'Ground Operations', 'Ground Operations Manager', '2019-11-15', 5);

-- Insert Sample Baggage
INSERT INTO baggage (booking_id, weight, description) VALUES
(1, 15.5, 'Black suitcase'),
(2, 12.0, 'Blue duffel bag'),
(3, 20.0, 'Large trolley bag'),
(4, 8.5, 'Small backpack'),
(5, 18.0, 'Brown suitcase');

-- Insert Sample Flight Status
INSERT INTO flight_status (flight_id, status) VALUES
(1, 'On Time'),
(2, 'Delayed'),
(3, 'On Time'),
(4, 'Boarding'),
(5, 'Scheduled');

-- Insert Sample Customer Feedback
INSERT INTO customer_feedback (passenger_id, flight_id, rating, comment) VALUES
(1, 1, 5, 'Excellent service and on-time departure'),
(2, 2, 4, 'Good experience, slight delay'),
(3, 3, 5, 'Very comfortable flight'),
(4, 4, 4, 'Good service, could improve food quality'),
(5, 5, 5, 'Perfect journey');

-- Insert Sample Login Users
INSERT INTO login (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('rahul', 'pass123', 'user'),
('priya', 'pass456', 'user'),
('amit', 'pass789', 'user'),
('neha', 'pass101', 'user');

-- Link Aircraft to Flights
INSERT INTO flight_aircraft (flight_id, aircraft_id) VALUES
(1, 1), -- Boeing 737-800 for AI-101
(2, 2), -- Airbus A320 for 6E-201
(3, 3), -- Boeing 777-300ER for UK-301
(4, 4), -- Airbus A321 for AI-401
(5, 5); -- Boeing 787-9 for SG-501 