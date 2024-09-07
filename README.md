# Bookshop V2 - CSV File I/O Implementation

This is the second version of the Bookshop project, evolving from in-memory storage to CSV file-based data persistence.

## Features

- Book management (add, remove, search, update)
- User management
- Book lending system
- Data persistence using CSV files
- Improved error handling

## Project Structure
![IO Structure v2!](src/main/resources/V2-IOstructure.png)

- `Book.java`: Represents a book with properties like title, author, ISBN, and availability.
- `BookException.java`: Custom exception for book-related errors.
- `Bookshop.java`: Main class managing bookshop operations and CSV file I/O.
- `FileException.java`: Custom exception for file-related errors.
- `Main.java`: Entry point of the application with an enhanced CLI interface.
- `BookList.csv`: CSV file for storing book data.

## How to Run

1. Ensure you have Java JDK 8 or higher installed.
2. Compile the Java files:
   `java org/evpro/bookshopV2/*.java`
3. Run test: `java test/java/BookshopTest`

## Usage

The application provides an improved command-line interface. Follow the on-screen prompts to:

1. Add a new book
2. Search for a book by ISBN
3. Borrow a book
4. Return a book
5. View all books

## CSV File Format

The `BookList.csv` file uses the following format:

<em><strong>title,author,ISBN,available</strong><br>
The Great Gatsby,F. Scott Fitzgerald,9780743273565,true<br>
To Kill a Mockingbird,Harper Lee,9780446310789,false</em>

## Improvements from V1

- Data persistence: Books are now stored in a CSV file, allowing data to be retained between program executions.
- Enhanced error handling: Introduction of `FileException` for better file operation error management.
- Improved book management: Added functionality to view all books.
- More robust CSV parsing and writing.

## Limitations

- Still uses a simple data structure, not suitable for large datasets.
- No user authentication or authorization.
- Limited concurrent access handling.

## Testing

Run the `BookshopTest.java` to execute unit tests for the Bookshop class, including new tests for file I/O operations.

## Future Improvements

- Implement database storage for better data management (addressed in V3)
- Add user authentication and authorization
- Improve concurrent access handling

## Contributing

Contributions to enhance this CSV-based version are welcome. Please ensure your code adheres to the existing style, includes appropriate tests, and maintains or improves the current CSV handling capabilities.
