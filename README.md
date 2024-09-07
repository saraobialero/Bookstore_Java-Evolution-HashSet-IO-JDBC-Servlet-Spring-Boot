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