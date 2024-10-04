# 📚 Bookshop V1 - Basic HashSet Implementation

This is the first iteration of the Bookshop project, built using Java's `HashSet` for simple in-memory data management. It provides basic functionality for managing books and users, including adding, removing, and searching for books.

## 📋 Table of Contents

1. [Project Overview](#-project-overview)
2. [Features](#-features)
3. [Technologies Used](#-technologies-used)
4. [Project Structure](#-project-structure)
5. [How to Run](#-how-to-run)
6. [Limitations](#-limitations)
7. [Testing](#-testing)
8. [Future Improvements](#-future-improvements)
9. [Contributing](#-contributing)

---

## 📖 Project Overview

**Bookshop V1** is a command-line Java application designed to manage a collection of books and basic user interactions. It uses Java's `HashSet` for in-memory storage, ensuring fast and efficient operations. This first version is intended as a starting point, with future iterations adding persistence and advanced features.

---

## ✨ Features

- **Book Management**
    - Add new books (title, author, ISBN, availability).
    - Remove books from the catalog.
    - Search for books by ISBN.

- **User Management**
    - Simple user interactions (borrow, return books).

- **In-memory Storage**
    - Uses `HashSet` for fast in-memory data storage and management.

---

## 🛠 Technologies Used

| Category   | Technology                                                                                                                                     |
|------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| **Language** | [![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) |
| **Collections** | [![HashSet](https://img.shields.io/badge/HashSet-white.svg)](https://docs.oracle.com/javase/8/docs/api/java/util/HashSet.html)
|
| **Testing**    | [![JUnit5](https://img.shields.io/badge/JUnit-5.7.0-green.svg)](https://junit.org/junit5/)
|

---

## 📂 Project Structure

The project follows a clean, modular structure for ease of navigation:

```
src/
├── main/
│   ├── java/org/evpro/bookshopV1/
│   │    ├── Book
│   │    ├── BookException
│   │    └── Bookshop
└──  test/java/
```

- `Book.java`: Represents a book with properties like title, author, ISBN, and availability.
- `BookException.java`: Custom exception for book-related errors.
- `Bookshop.java`: Main class managing the bookshop operations.

---

## 🚀 How to Run

To get started, ensure you have Java 8 or higher installed, then follow these steps:

1. **Clone the repository**:

   ```bash
   git clone https://github.com/your-username/bookshop-v1.git
   cd bookshop-v1
   ```
2. **Compile the Java files:**
```
javac src/main/java/org/evpro/bookshopV1/*.java
```
3. Run the unit tests:
```
javac -cp .:junit-5.jar test/java/BookshopTest.java
java -cp .:junit-5.jar org.junit.runner.JUnitCore BookshopTest
```

---

## 💻 Usage

The application provides a simple command-line interface. Follow the on-screen prompts to:

1. Add a new book
2. Search for a book by ISBN
3. Borrow a book
4. Return a book
5. Exit the program

---

## ⚠️  Limitations

- *Data Persistence:* This version does not save data to files or databases. All data is stored in-memory, meaning it will be lost when the program exits. 
- *Basic User Management:* No authentication or user roles are implemented in this version. 
- *Error Handling:* Limited error handling for invalid inputs and operations.

---

## ✅ Testing

Unit tests for this version are implemented using JUnit 5. You can find the test cases in `test/java/BookshopTest.java. These tests ensure that basic operations like adding, removing, and searching for books work as expected.

---

## 🚀 Future Improvements

- Implement data persistence (addressed in V2)
- Add more robust error handling
- Expand book and user management features

---

## 🫶 Contributing

Contributions to improve this basic version are welcome. Please ensure your code adheres to the existing style and includes appropriate tests.