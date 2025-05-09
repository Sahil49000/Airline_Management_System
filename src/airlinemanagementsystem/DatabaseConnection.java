package airlinemanagementsystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "database.properties";
    private String url;
    private String user;
    private String password;

    public DatabaseConnection() {
        loadConfig();
    }

    private void loadConfig() {
        Properties props = new Properties();
        InputStream inputStream = null;
        boolean configLoaded = false;

        try {
            // Try multiple locations for the properties file
            String[] possiblePaths = {
                CONFIG_FILE,
                "src/" + CONFIG_FILE,
                "src/airlinemanagementsystem/" + CONFIG_FILE
            };

            for (String path : possiblePaths) {
                try {
                    inputStream = new FileInputStream(path);
                    props.load(inputStream);
                    System.out.println("Found database.properties at: " + path);
                    configLoaded = true;
                    break;
                } catch (IOException e) {
                    // Continue to next path
                }
            }

            // If not found in file system, try classpath
            if (!configLoaded) {
                inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
                if (inputStream != null) {
                    props.load(inputStream);
                    System.out.println("Found database.properties in classpath");
                    configLoaded = true;
                }
            }

            if (!configLoaded) {
                System.out.println("Warning: database.properties not found. Using default configuration.");
            }

            // Load properties with defaults
            url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/airlinemanagement_updated");
            user = props.getProperty("db.user", "root");
            password = props.getProperty("db.password", "admin");

        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            // Set default values
            url = "jdbc:mysql://localhost:3306/airlinemanagement_updated";
            user = "root";
            password = "admin";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("Error closing input stream: " + e.getMessage());
                }
            }
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Database connection established successfully.");
                System.out.println("Connected to: " + url);
                return connection;
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Failed to establish database connection: " + e.getMessage(), e);
        }
        throw new SQLException("Failed to establish database connection: Unknown error");
    }
}
