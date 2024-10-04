# 📚 Bookshop V2 - CSV File I/O Implementation

This is the second iteration of the Bookshop project, transitioning from in-memory storage to a CSV file-based system for persistent data storage.


## 📋 Table of Contents

1. [Project Overview](#-project-overview)
2. [Features](#-features)
3. [Technologies Used](#-technologies-used)
4. [Project Structure](#-project-structure)
5. [How to Run](#-how-to-run)
6. [CSV File Format](#-csv-file-format)
7. [Limitations](#-limitations)
8. [Testing](#-testing)
9. [Future Improvements](#-future-improvements)
10. [Contributing](#-contributing)



## 📖 Project Overview

**Bookshop V2** evolves from the initial in-memory version, incorporating file-based data persistence via CSV files. This upgrade allows the bookshop to store book information between sessions, providing more reliability for long-term usage.



## ✨ Features

- **Book Management**:
   - Add new books (title, author, ISBN, availability).
   - Remove books from the catalog.
   - Search for books by ISBN.
   - Update book details (availability, title, etc.).
   - View all books.

- **User Management**:
   - Basic user interaction for borrowing and returning books.

- **Data Persistence**:
   - All book data is stored in a CSV file (`BookList.csv`), ensuring that data persists across program executions.



## 🛠 Technologies Used

| Category           | Technology                                                                                                                                       |
|--|--|
| **Language**       | [![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) |
| **Data Storage**   | ![CSV](https://img.shields.io/badge/CSV-white.svg)                                                                                               |
| **Exception Handling** | ![CE](https://img.shields.io/badge/Custom_Exception-blue.svg)                                                                                    |
| **Testing**        | [![JUnit5](https://img.shields.io/badge/JUnit-5.7.0-green.svg)](https://junit.org/junit5/)                                                       |



## 📂 Project Structure

The project follows a modular structure, with a focus on separation of concerns for better scalability:


```
src/
├── main/
│   ├── java/org/evpro/bookshopV2/
│   │    ├── Book
│   │    ├── BookException
│   │    ├── Bookshop
│   │    └── FileException
│   └── resources/
│       └── BookList.csv
└──  test/java/
```


- `Book.java`: Represents a book with properties like title, author, ISBN, and availability.
- `BookException.java`: Custom exception for book-related errors.
- `Bookshop.java`: Main class managing bookshop operations and CSV file I/O.
- `FileException.java`: Custom exception for file-related errors.
- `Main.java`: Entry point of the application with an enhanced CLI interface.
- `BookList.csv`: CSV file for storing book data.



## 🚀 How to Run

To get started, ensure you have Java JDK 8 or higher installed, then follow these steps:

1. **Clone the repository**:

```bash
git clone https://github.com/your-username/bookshop-v2.git
cd bookshop-v2
```
2. **Compile the Java files:**
```bash
javac src/main/java/org/evpro/bookshopV2/*.java
```
3. **Run the tests::**
```bash
javac -cp .:junit-5.jar test/java/BookshopTest.java
java -cp .:junit-5.jar org.junit.runner.JUnitCore BookshopTest
```



## 💻 Usage

The application provides an improved command-line interface. Follow the on-screen prompts to:

1. Add a new book
2. Search for a book by ISBN
3. Borrow a book
4. Return a book
5. View all books



## 📄 CSV File Format

The `BookList.csv` file uses the following format:

```
title,author,ISBN,available
The Great Gatsby,F. Scott Fitzgerald,9780743273565,true
To Kill a Mockingbird,Harper Lee,9780446310789,false
```



## ❗️ Improvements from V1

- Data persistence: Books are now stored in a CSV file, allowing data to be retained between program executions.
- Enhanced error handling: Introduction of `FileException` for better file operation error management.
- Improved book management: Added functionality to view all books.
- More robust CSV parsing and writing.



## ⚠️ Limitations

- **Data Structure:** Still uses a simple data structure, not suitable for large datasets.
- **Authentication:** No user authentication or authorization.
- **Concurrency:** Limited concurrent access handling.



## ✅ Testing

Unit tests are implemented using JUnit 5. You can find the tests in the `BookshopTest.java` file. These tests cover: 

* Adding, removing, and searching for books. 
* Borrowing and returning books. 
* File I/O operations (reading from and writing to the CSV file).



## 🚀 Future Improvements

- Implement database storage for better data management (addressed in V3)
- Add user authentication and authorization
- Improve concurrent access handling



## 🫶 Contributing

Contributions are welcome! Please ensure that your code adheres to the existing style, includes appropriate tests, and maintains or improves the current CSV handling functionality.
