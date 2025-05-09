# Airline Management System ✈️

## About
Airline Management System is a comprehensive desktop application designed to streamline the management of airline operations, from customer bookings to flight schedules. This system enables users to book flights, view flight details, manage bookings, and handle passenger information with ease. Key features include:
- **User-Friendly Interface**: Simplifies the booking process with an interactive and accessible design.
- **Booking System**: Ensures safe storage and management of passenger data with unique PNR numbers for every booking.
- **Passenger Management**: Maintains a structured database of passenger details, allowing efficient management and retrieval of information.
- **Admin Login and Security**: Restricts sensitive operations to authorized personnel, ensuring data privacy and integrity.

## File Descriptions
- **AddCustomer.java**: Allows the addition of new customer details to the system.
- **BoardingPass.java**: Generates a boarding pass for each booking.
- **BookFlight.java**: Handles flight booking operations.
- **Cancel.java**: Provides functionality for canceling a reservation.
- **DatabaseConnection.java**: Establishes a connection with the SQL database.
- **FlightInfo.java**: Shows information about available flights.
- **Home.java**: Serves as the main entry point for the application after login.
- **JourneyDetails.java**: Maintains information on customers travel details.
- **Login.java**: Manages the login process for users.
- **SQL.sql**: Contains SQL code for setting up the database structure.
- **Sample.sql**: Contains SQL sample for setting up the database structure.

## Tech Stack
- *Java*: For building the core functionalities and user interface of the desktop application.
- *MySQL*: Manages and stores data related to passengers, flights, bookings, and login information securely.
- *JDBC*: Enables communication between the Java application and MySQL database for data retrieval and manipulation.
- *Java Swing or JavaFX*: Provides a graphical user interface for an intuitive and user-friendly experience.
- *Git*: For version control and collaboration during development.

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/as6769-2004/Airline-Reservation-System.git
   ```

2. **Install Java**
   - Download and install the Java Development Kit (JDK) from the official Oracle website: [Java JDK Download](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
   - After installation, verify your Java installation by running:
     ```bash
     java -version
     ```

3. **Download MySQL**
   - Go to the official MySQL website: [MySQL Community Server Download](https://dev.mysql.com/downloads/installer/)
   - Select your operating system and download the appropriate installer.

4. **Install MySQL**
   - Run the installer and follow the on-screen instructions.
   - During installation:
     - Choose a setup type (Developer Default is recommended).
     - Set the root password (remember this password).
     - Enable MySQL Server as a service.

5. **Verify MySQL Installation**
   ```bash
   mysql --version
   ```

6. **Access MySQL Command Line**
   ```bash
   mysql -u root -p
   ```

7. **Create the Database**
   Copy and execute the following SQL to set up the schema:

   

   - Use sample data from *[Database.sql](https://github.com/as6769-2004/Airline-Reservation-System/blob/main/SQL_Updated.sql)* if applicable.

✅ *That's it!*

## Screenshots

<!-- Preserve your existing images here -->
