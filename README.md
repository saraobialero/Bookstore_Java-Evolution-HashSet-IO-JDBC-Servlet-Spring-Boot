# 📚 Bookshop V3 - JDBC Database Integration

This is the third version of the Bookshop project, evolving from CSV file storage to a relational database using JDBC for efficient data management.

## 📋 Table of Contents

1. [Project Overview](#-project-overview)
2. [Features](#-features)
3. [Technologies Used](#-technologies-used)
4. [Project Structure](#-project-structure)
5. [Prerequisites](#-prerequisites)
6. [Setup](#-setup)
7. [How to Run](#-how-to-run)
8. [Database Schema](#-database-schema)
9. [Limitations](#-limitations)
10. [Testing](#-testing)
11. [Future Improvements](#-future-improvements)
12. [Contributing](#-contributing)
13. [Notes](#-notes)



## 📖 Project Overview

**Bookshop V3** transitions from CSV-based storage to a robust relational database system, leveraging JDBC for database interactions. This version improves data persistence, performance, and security, making it more suitable for larger datasets and multi-user environments.



## ✨ Features

- **Enhanced Book Management**:
   - Add, remove, search, and update book records in a database.

- **User Management**:
   - Improved user interactions and data persistence in the database.

- **Advanced Book Lending System**:
   - Track borrowing and returning of books in a more structured and scalable way.

- **JDBC Integration**:
   - Seamless integration with MySQL for database management.

- **Custom Exception Handling**:
   - Custom exceptions for handling errors such as database connection issues or SQL execution problems.

- **Connection Pooling**:
   - Efficient management of database connections to enhance performance.

- **Logging**:
   - Integrated logging system for better monitoring and debugging (configured via `logback.xml`).



## 🛠 Technologies Used

| Category           | Technology                                                                                                                            |
|--|--|
| **Language**       | [![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) |
| **Database**       | [![MySQL](https://img.shields.io/badge/MySQL-blue.svg)](https://www.mysql.com/)                                                       |
| **JDBC**           | [![JDBC](https://img.shields.io/badge/JDBC-white.svg)](https://docs.oracle.com/javase/tutorial/jdbc/)                                 |
| **Build Tool**     | [![Maven](https://img.shields.io/badge/Maven-3.6.3-green.svg)](https://maven.apache.org/)                                             |
| **Logging**        | [![Logback](https://img.shields.io/badge/Logback-1.2.3-red.svg)](http://logback.qos.ch/)                                              |
| **Testing**        | [![JUnit5](https://img.shields.io/badge/JUnit-5.7.0-green.svg)](https://junit.org/junit5/)                                            |



## 📂 Project Structure


```
src/
├── main/
│   ├── java/org/evpro/bookshopV3/
│   │           ├── db/
│   │           │   └── DatabaseManager
│   │           ├── exception/
│   │           │   ├── BookException
│   │           │   ├── DataBaseException
│   │           │   └── ErrorResponse
│   │           └──  model/
│   │                ├── Book
│   │                ├── Bookshop
│   │                └── PublicBookView
│   │         
│   └── resources/
│       ├── data.sql
│       ├── database.properties
│       ├── logback.xml
│       └── schema.sql
└──  test/java/
                    
``` 

- `DatabaseManager.java`: Manages database connections and operations
- `Exception classes`: Custom exceptions for better error handling
- `Model classes`: Represent data entities and business logic
- `SQL files`: Database schema and initial data
- `database.properties`: Database configuration
- `logback.xml`: Logging configuration




## 💻 Prerequisites
- Java JDK 8 or higher
- MySQL Server
- Maven (for dependency management)

## ⚙️ Setup

Follow these steps to set up the project:

1. **Configure the Database**:
   - Update the `database.properties` file with your MySQL database credentials:
     ```
     db.url=jdbc:mysql://localhost:3306/bookshop
     db.username=root
     db.password=yourpassword
     ```

2. **Set up the Database**:
   - Run `schema.sql` to create the necessary tables:
     ```bash
     mysql -u root -p < src/main/resources/sql/schema.sql
     ```
   - Run `data.sql` to populate the database with initial data:
     ```bash
     mysql -u root -p < src/main/resources/sql/data.sql
     ```

3. **Build the Project**:
   - Compile the project using Maven:
     ```bash
     mvn clean compile
     ```



## 🚀 How to Run

1. **Run Unit Tests**:
   - Ensure MySQL is running and that your database is properly configured.
   - Run the tests, including database-related tests:
       ```bash
       mvn test
       ```


## ❗️ Key Improvements from V2

- Database: Efficient data storage and retrieval
- JDBC integration: Robust database operations
- Connection pooling: Improved performance for database connections
- Prepared statements: Protection against SQL injection
- Logging: Enhanced debugging and monitoring capabilities



## 🗃 Database Schema

- `books`: Stores book information



## ✅ Testing

Run `mvn test` to execute the unit tests, including database operation tests.

## ⚠️ Limitations

- **No User Interface**: This version operates as a back-end system with no command-line interface or graphical interface.
- **Security**: Basic security measures are in place, but there are no advanced security features like encryption or authentication.



## 🚀 Future Improvements

- Implement a web interface (addressed in V4)
- Enhance security features
- Optimize database queries for large datasets

## 🫶 Contributing

Contributions are welcome! Please ensure your code follows the existing structure, includes appropriate tests, and adheres to good JDBC practices.

## 📝  Notes

- This version uses a properties file for database configuration. Ensure `database.properties` is properly set up before running the application.
- The `DatabaseManager` class includes methods for database initialization, which can be useful for testing and initial setup.