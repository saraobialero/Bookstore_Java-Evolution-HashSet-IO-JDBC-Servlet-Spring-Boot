# 📚 Bookshop V5 - RESTful API with Spring Boot


This is the fifth version of the Bookshop project, evolving into a RESTful API using Spring Boot. It represents a significant advancement from the previous Servlet-based implementation, offering improved scalability, security, and ease of development.

## 📋 Table of Contents

- [Domain Description](#-domain-description)
- [Project Structure](#-project-structure)
- [Entity-Relationship Diagram](#-er-diagram)
- [Prerequisites](#-prerequisites)
- [Setup](#-setup)
- [Dependency Management](#-dependency-management)
- [Authentication and Authorization](#-authentication-and-authorization)
- [Security](#-security)
- [Swagger for API endpoints](#-swagger-for-testing-api-endpoints)
- [Error Handling](#-error-handling)
- [Monitoring and Logging](#-monitoring-and-logging)
- [Performance Considerations](#-performance-considerations)
- [Contributing](#-contributing)
- [limitations](#-limitations)
- [Testing](#-testing)
- [Improvement from v4](#improvements-from-v4)
- [Changelog](#-changelog)
- - [Deployment](#-deployment)
- [FAQ](#-faq)

## 📖 Project Overview

**Bookshop V5** is a RESTful API built with Spring Boot that supports comprehensive book and user management for libraries or bookstores. It represents a significant improvement from previous versions, shifting from a servlet-based architecture to a fully scalable, secure, and modular API-driven solution. The application handles essential operations such as user authentication (with role-based access), managing books, processing loans, and maintaining shopping carts, while also offering robust security with JWT-based authentication and role-based access control.

The modular design of Bookshop V5 improves maintainability, scalability, and ease of development, making it suitable for real-world scenarios involving multiple users, roles, and business logic complexities.


## ✨ Features

- RESTful API endpoints for comprehensive book and user management
- Enhanced security with JWT-based authentication and authorization
- Improved data access and management using Spring Data JPA
- Advanced error handling with global exception management
- Swagger UI for API documentation and testing
- Modular architecture for improved maintainability and scalability

## 🛠 Technologies Used

| Category                     | Technology                                                                      |
|------------------------------|---------------------------------------------------------------------------------|
| Programming Language          | ![Java](https://img.shields.io/badge/Java-17-orange)                            |
| Framework                     | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.4-green)          |
| Database Access               | ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-2.5.4-blue) |
| Security                      | ![Spring Security](https://img.shields.io/badge/Spring%20Security-5.5.2-yellow) |
| Stateless Authentication      | ![JWT](https://img.shields.io/badge/JWT-JSON%20Web%20Token-red)                 |
| Database                     | ![MySQL](https://img.shields.io/badge/MySQL-8.0+-blueviolet)                    |
| API Documentation             | ![Swagger](https://img.shields.io/badge/Swagger-3.0.0-ff69b4)                   |
| Build Tool                   | ![Maven](https://img.shields.io/badge/Maven-3.8.1-orange)                       |
| Logging Framework             | ![SLF4J](https://img.shields.io/badge/SLF4J-1.7.30-lightgray)                   |
| Logging Framework             | ![Logback](https://img.shields.io/badge/Logback-1.2.3-yellowgreen)              |


## 🏢 Domain Description

Bookshop V5 is a comprehensive book management system designed for libraries or bookstores. It allows for efficient management of books, user accounts, loans, and shopping carts. The system supports various operations such as adding new books, managing user roles, processing book loans, and handling return processes.


## 🏗 Project Structure

```
src/
├── main/
│   ├── java/org/evpro/bookshopV5/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── exception/
│   │   ├── filter/
│   │   ├── model/
│   │   │   └── DTO/
│   │   ├── repository/
│   │   ├── service/
│   │   └── utils/
│   └── resources/
│       └── application.properties
└── test/
    └── java/org/evpro/bookshopV5/
```



## 📊 ER Diagram

Here's a simplified Entity-Relationship diagram of the Bookshop V5 system:

```mermaid
erDiagram
    USER ||--o{ LOAN : "makes"
    USER ||--o| CART : "has"
    USER }o--|| ROLE : "has"
    BOOK ||--o{ LOAN_DETAILS : "included in"
    BOOK ||--o{ CART_ITEM : "added to"
    LOAN ||--|{ LOAN_DETAILS : "contains"
    CART ||--|{ CART_ITEM : "contains"
    
    USER {
        int id PK
        string name
        string email
        string surname
        string password
        boolean active
    }
    BOOK {
        int id PK
        string title
        string author
        date publicationYear
        string description
        string award
        enum genre
        string ISBN
        int quantity
        boolean available
    }
    LOAN {
        int id PK
        int user_id FK
        date loanDate
        date dueDate
        date returnDate
        enum status
    }
    CART {
        int id PK
        int user_id FK
        date createdDate
    }
    LOAN_DETAILS {
        int id PK
        int loan_id FK
        int book_id FK
        int quantity
    }
    CART_ITEM {
        int id PK
        int cart_id FK
        int book_id FK
        int quantity
    }
    ROLE {
        int id PK
        enum role
    }
    
```    


This diagram shows the main entities (User, Book, Cart, Loan) and their relationships.



## 🛠 Prerequisites

- Java JDK 17 or higher
- Maven 3.6+
- MySQL Server 8.0+
- Git (for version control)


## 🚀 Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/bookshop-v5.git
   ```
2. Navigate to the project directory:
   ```
   cd bookshop-v5
   ```
3. Create a MySQL database named `bookshop_db_v5`
4. Update `src/main/resources/application.properties` with your MySQL credentials
5. Build the project:
   ```
   mvn clean install
   ```
6. Run the application:
   ```
   mvn spring-boot:run
   ```


## 📦 Dependency Management

Bookshop V5 uses Maven for dependency management. The `pom.xml` file in the project root defines all necessary dependencies.

To manage project dependencies:

1. Ensure Maven is installed on your system.
2. To download dependencies and compile the project, run:
   ```
   mvn clean install
   ```
3. To check for dependency updates:
   ```
   mvn versions:display-dependency-updates
   ```
4. To update dependencies, manually edit versions in `pom.xml`, then run `mvn clean install` again.

Regularly check for dependency updates to keep the project secure and up-to-date.



## 🔐 Authentication and Authorization

- JWT-based authentication
- Role-based access control (USER and ADMIN roles)
- Stateless authentication for improved scalability



## 🛡 Security

Security in Bookshop V5 is primarily implemented through Spring Security and JWT (JSON Web Tokens).

### JWT Authentication

1. JWT is used for stateless session management.
2. JWT configuration is in `application.properties`:
   ```
   application.security.jwt-secret-key=your_secret_key_here
   application.security.jwt-expired-access-token=86400000
   application.security.jwt-expired-refresh-token=172800000
   ```
3. The `JwtUtils` class handles token creation and validation.

### CORS Configuration

CORS (Cross-Origin Resource Sharing) is configured in `WebSecurityConfig.java`. Ensure proper configuration of allowed origins for cross-origin requests.

### Endpoint Protection

Endpoints are protected using annotations like `@PreAuthorize` in controllers, specifying required roles for each endpoint.

### Security Best Practices

- Keep security dependencies up-to-date
- Use HTTPS in production
- Implement rate limiting to prevent brute-force attacks
- Conduct regular security audits


## 🛣 Swagger for Testing API Endpoints

### How to start
To testing the API Endpoints you can connect to the Swagger URL:
http://localhost:8080/swagger-ui/index.html#/

If you use my DbInitializer, you can sign in with these credentials:


**ADMIN:** This access give you possibility to test all application functions.
```json
{
"email": "admin@bookshop.com",
"password": "adminPass123!?"
}
```

**USER:** This access give you possibility to test only user functions of application.
```json
{
"email": "john@example.com",
"password": "user-Pass123*"
}
```

After sign in with correct credential, you must copy the `access_token` that you find in the `SuccessResponse`, click on the top of page in ` Authorize` and paste the content in `Value:`.
If the token is correct, now you can test the entire application functions on Swagger.

You can generate Swagger api-docs here: http://localhost:8080/v3/api-docs



## 🚦 Error Handling

- Global exception handling for consistent error responses
- Custom exceptions for specific error scenarios
- Detailed error messages and appropriate HTTP status codes

## 📊 Monitoring and Logging

Bookshop V5 uses SLF4J with Logback for logging. Logging configuration is in `logback-spring.xml`.

### Logging Configuration

- Development and test environments: Logs are written to both console and file.
- Production environment: Logs are primarily written to files with rotation.

### Log Levels

- INFO: General application flow
- ERROR: Unexpected errors and exceptions
- DEBUG: Detailed information for debugging (development only)

### Monitoring

- Consider integrating with monitoring tools like Prometheus and Grafana for production environments.
- Implement health check endpoints for system status monitoring.



## 🚀 Performance Considerations

- Implement caching mechanisms for frequently accessed data
- Optimize database queries and indexing
- Consider using connection pooling for database connections
- Implement pagination for large data sets in API responses



## ⚠️ Limitations

While Bookshop V5 introduces significant improvements, there are some limitations:

1. **No Front-End Implementation**: The project is a pure back-end solution. There is no user interface, so interaction happens through API calls or via Swagger UI.

2. **Limited Scalability Out of the Box**: Though improved from V4, the default setup might still require further optimizations (e.g., load balancing, caching strategies) for handling large-scale traffic or enterprise use.

3. **Basic Role Management**: The application currently supports only two roles (ADMIN and USER), limiting the granularity of role-based access control.

4. **No Advanced Caching**: There's no built-in caching mechanism to optimize frequent database calls, which could affect performance in high-traffic scenarios.

5. **Simple Loan Management**: Bookshop V5 supports basic loan functionality but lacks features such as notifications for overdue returns or automated renewal options.

## ✅ Testing

Run tests using:
```
mvn test
```

Consider implementing:
- Unit tests for service and utility classes
- Integration tests for repository classes
- End-to-end tests for API endpoints

## ❗️Improvements from V4

Bookshop V5 introduces several major upgrades over the previous version (V4):

1. **Migration to Spring Boot**: Shifting from a servlet-based system to Spring Boot significantly improves scalability, development speed, and ease of use.

2. **JWT-based Security**: JWT provides stateless, scalable authentication, eliminating the need for server-side session management used in V4. This leads to better performance in distributed systems and microservice architectures.

3. **Spring Data JPA**: Replaces the manual JDBC and DAO pattern from V4 with an ORM-based approach, simplifying database operations and improving maintainability.

4. **RESTful API Design**: The V5 API strictly follows REST principles, improving clarity and standardization, while V4 used a more traditional servlet approach.

5. **Swagger Integration**: Added Swagger UI for API testing and documentation, making it easier to interact with the API and understand the available endpoints.

6. **Modular Structure**: Bookshop V5 has a more structured and modular project layout, making it easier to maintain and scale the application.

7. **Error Handling**: Global exception handling with Spring Boot provides more consistent and detailed error responses across the application compared to the V4 custom exceptions approach.

8. **Improved Dependency Management**: Maven is used more extensively to handle project dependencies, offering better integration and ease of updating packages.



## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch for your feature
3. Commit your changes
4. Push to your branch
5. Create a Pull Request

Please ensure your code follows the existing structure, includes appropriate tests, and adheres to the project's coding standards.



## 📜 Changelog

- v5.0.0 (2023-10-01): Initial release of Bookshop V5
    - Migrated to Spring Boot
    - Implemented JWT authentication
    - Added comprehensive API documentation with Swagger


## 🌐 Deployment

For production deployment:

1. Build the project:
   ```
   mvn clean package
   ```
2. The built JAR file will be in the `target/` directory
3. Deploy the JAR file to your production server
4. Ensure proper production configurations (database, security, etc.)
5. Run the application:
   ```
   java -jar bookshop-v5.jar
   ```

Consider using containerization (e.g., Docker) for easier deployment and scaling.

## ❓ FAQ

Q: How do I reset a user's password?
A: Use the admin endpoint `/api/v1/users/{userId}/reset-password`.

Q: Can I use a different database?
A: Yes, update the database configuration in `application.properties` and include the appropriate JDBC driver in `pom.xml`.
