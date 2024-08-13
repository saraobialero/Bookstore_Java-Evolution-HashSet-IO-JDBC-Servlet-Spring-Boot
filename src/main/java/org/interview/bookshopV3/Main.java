package org.interview.bookshopV3;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
/*
        final String FILE_PATH = "src/main/java/org/interview/bookshopV2/BookList.csv";
        Bookshop bookshop = new Bookshop();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        try {
            bookshop.readFile(FILE_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found. " + e.getMessage());
            return;
        }

        while (!exit) {
            System.out.println("Menu:");
            System.out.println("1. Add book");
            System.out.println("2. Search book by ISBN Code");
            System.out.println("3. Take a book");
            System.out.println("4. Return a book");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Author: ");
                        String author = scanner.nextLine();
                        System.out.print("ISBN: ");
                        String ISBN = scanner.nextLine();
                        Book newBook = new Book(title, author, ISBN, true);
                        if (bookshop.addBook(newBook, FILE_PATH)) {
                            System.out.println("Book added successfully!");
                        } else {
                            System.out.println("The book already exists...");
                        }
                        break;
                    case 2:
                        System.out.print("ISBN: ");
                        String searchISBN = scanner.nextLine();
                        try {
                            Book foundBook = bookshop.searchBookById(searchISBN)
                                    .orElseThrow(() -> new BookException("Book not found"));
                            System.out.println("Book Found: " + foundBook.getTitle() + " Author: " + foundBook.getAuthor());
                        } catch (BookException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.print("ISBN: ");
                        String borrowISBN = scanner.nextLine();
                        if (bookshop.giveBook(borrowISBN, FILE_PATH)) {
                            System.out.println("Book borrowed successfully!");
                        } else {
                            System.out.println("The book is not available...");
                        }
                        break;
                    case 4:
                        System.out.print("ISBN: ");
                        String returnISBN = scanner.nextLine();
                        if (bookshop.returnBook(returnISBN, FILE_PATH)) {
                            System.out.println("Book returned successfully!");
                        } else {
                            System.out.println("The book wasn't borrowed...");
                        }
                        break;
                    case 5:
                        exit = true;
                        System.out.println("You have left the system");
                        break;
                    default:
                        System.out.println("Not a valid option, please try again...");
                }
            } catch (FileException e) {
                System.out.println("Error with file operations: " + e.getMessage());
            } catch (BookException e) {
                System.out.println("Error with book operations: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();*/
    }
}




