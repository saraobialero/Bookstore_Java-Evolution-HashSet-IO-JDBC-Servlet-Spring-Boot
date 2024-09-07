# Bookshop V1 - Basic HashSet Implementation

This is the first version of the Bookshop project, implementing basic functionality using Java's HashSet for in-memory data management.

## Features

- Basic book management (add, remove, search)
- Simple user management
- In-memory data storage using HashSet

## Project Structure
![Project structure: v1!](src/main/resources/V1-Hashset_structure.png)

- `Book.java`: Represents a book with properties like title, author, ISBN, and availability.
- `BookException.java`: Custom exception for book-related errors.
- `Bookshop.java`: Main class managing the bookshop operations.
- `Main.java`: Entry point of the application with a simple CLI interface.

## How to Run

1. Ensure you have Java JDK 8 or higher installed.
2. Compile the Java files:
`javac org/evpro/bookshopV1/*.java`
3. Run test:
`javac test/java/BookshopTest`

## Usage

The application provides a simple command-line interface. Follow the on-screen prompts to:

1. Add a new book
2. Search for a book by ISBN
3. Borrow a book
4. Return a book
5. Exit the program

## Limitations

- Data is not persistent and will be lost when the program exits.
- Limited error handling and input validation.
- No user authentication or authorization.

## Testing

Run the `BookshopTest.java` to execute unit tests for the Bookshop class.

## Future Improvements

- Implement data persistence (addressed in V2)
- Add more robust error handling
- Expand book and user management features

## Contributing

Contributions to improve this basic version are welcome. Please ensure your code adheres to the existing style and includes appropriate tests.