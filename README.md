# Bookshop V3 - JDBC Database Integration

This is the third version of the Bookshop project, evolving from CSV file storage to a relational database using JDBC.

## Features

- Enhanced book management (add, remove, search, update)
- Improved user management
- Advanced book lending system
- Data persistence using MySQL database
- Robust error handling and custom exceptions
- Database connection management

## Project Structure
![Project Structure V3-JDBC!](src/main/resources/V3-JDBCstructure.png)

- `DatabaseManager.java`: Manages database connections and operations
- `Exception classes`: Custom exceptions for better error handling
- `Model classes`: Represent data entities and business logic
- `SQL files`: Database schema and initial data
- `database.properties`: Database configuration
- `logback.xml`: Logging configuration

## Prerequisites
- Java JDK 8 or higher
- MySQL Server
- Maven (for dependency management)

## Setup

1. Update `database.properties` with your MySQL credentials
2. Run `schema.sql` to create the necessary tables
3. Run `data.sql` to populate the database with sample data

## How to Run

1. Compile the project:
   `mvn clean compile`
2. Test the application: `src/main/test/*.java`

## Key Improvements from V2

- Database: Efficient data storage and retrieval
- JDBC integration: Robust database operations
- Connection pooling: Improved performance for database connections
- Prepared statements: Protection against SQL injection
- Logging: Enhanced debugging and monitoring capabilities

## Database Schema

- `books`: Stores book information

## Testing

Run `mvn test` to execute the unit tests, including database operation tests.

## Limitations

- No user interface (CLI or GUI)
- Basic security measures

## Future Improvements

- Implement a web interface (addressed in V4)
- Enhance security features
- Optimize database queries for large datasets

## Contributing

Contributions are welcome! Please ensure your code follows the existing structure, includes appropriate tests, and adheres to good JDBC practices.

## Notes

- This version uses a properties file for database configuration. Ensure `database.properties` is properly set up before running the application.
- The `DatabaseManager` class includes methods for database initialization, which can be useful for testing and initial setup.