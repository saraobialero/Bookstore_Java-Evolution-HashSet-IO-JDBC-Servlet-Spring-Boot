package org.evpro.bookshopV2;

import lombok.*;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Bookshop implements Serializable {
    private Set<Book> books;

    //File management
    public void readFile(String FILE_PATH) throws FileNotFoundException {
        books = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            if (scanner.hasNextLine()) {
                String firstLine = scanner.nextLine();
                if (firstLine.startsWith("title,author,ISBN,available")) {
                    while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        String title = data[0];
                        String author = data[1];
                        String ISBN = data[2];
                        boolean available = Boolean.parseBoolean(data[3]);

                        Book book = new Book(title, author, ISBN, available);
                        books.add(book);
                    }
                }
             }
            }
        }
    }
    private void addBookToFile(Book book, String FILE_PATH) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(bookToCsvString(book));
        }
    }
    public void updateCsvFile(String FILE_PATH) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("title,author,ISBN,available");
            for (Book book : books) {
                writer.println(bookToCsvString(book));
            }
        }
    }
    private String bookToCsvString(Book book) {
        return String.format("%s,%s,%s,%b",
                book.getTitle(),
                book.getAuthor(),
                book.getISBN(),
                book.isAvailable());
    }

    //Services methods
    public boolean addBook(Book book, String FILE_PATH) {
        boolean added = books.add(book);
        if (added) {
            try {
                addBookToFile(book, FILE_PATH);
            } catch (IOException e) {
                throw new FileException("File with file path " + FILE_PATH + " not found");
            }
        }
        return added;
    }
    public boolean giveBook(String ISBN, String FILE_PATH) {
        if (!isAvailable(ISBN)) {
            return false;
        }
        Book book = searchBookByISBN(ISBN).orElseThrow(() -> new BookException("Book with" + ISBN + " not found"));
        book.setAvailable(false);
        try {
            updateCsvFile(FILE_PATH);
        } catch (IOException e) {
            throw new FileException("File with file path " + FILE_PATH + " can't be updated");
        }
        return true;
    }
    public boolean returnBook(String ISBN, String FILE_PATH) {
        if (isAvailable(ISBN)) {
            return false;
        }
        Book book = searchBookByISBN(ISBN).orElseThrow(() -> new BookException("Book with " + ISBN + " not found"));
        book.setAvailable(true);
        try {
            updateCsvFile(FILE_PATH);
        } catch (IOException e) {
            throw new FileException("File with file path " + FILE_PATH + " can't be updated");
        }
        return true;
    }
    public Optional<Book> searchBookByISBN(String ISBN) {
        for (Book book: books) {
            if (book.getISBN().equals(ISBN)) {
                return Optional.of(book);
            }
        }
        throw new BookException("Book with ISBN " + ISBN + " not found");
    }
    private boolean isAvailable (String ISBN) {
        return searchBookByISBN(ISBN)
                .orElseThrow(() -> new BookException("Book with ISBN " + ISBN + " not found"))
                .isAvailable();
    }

}
