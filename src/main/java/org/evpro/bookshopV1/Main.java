package org.evpro.bookshopV1;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Book book1 = new Book("Il signore degli anelli", "J.R.R. Tolkien", "978-88-04-37061-1", true);
        Book book2 = new Book("Titolo", "J.R.R. Tolkien", "978-88-04-37061-2", true);
        Book book3 = new Book("Altro titolo", "J.R.R. Tolkien", "978-88-04-37061-3", true);

        Set<Book> books = new HashSet<>();
        books.add(book1);
        books.add(book3);
        books.add(book2);

        Bookshop bookshop = new Bookshop(books);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Menu:");
            System.out.println("1. Aggiungere un nuovo libro");
            System.out.println("2. Cercare un libro per ISBN");
            System.out.println("3. Prendere in prestito un libro");
            System.out.println("4. Restituire un libro");
            System.out.println("5. Uscire dal programma");
            System.out.print("Scegli un'opzione: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Titolo: ");
                    String title = scanner.nextLine();
                    System.out.print("Autore: ");
                    String author = scanner.nextLine();
                    System.out.print("ISBN: ");
                    String ISBN = scanner.nextLine();
                    Book newBook = new Book(title, author, ISBN, true);
                    if (bookshop.addBook(newBook)) {
                        System.out.println("Libro aggiunto con successo.");
                    } else {
                        System.out.println("Il libro esiste già.");
                    }
                    break;
                case 2:
                    System.out.print("ISBN: ");
                    String searchISBN = scanner.nextLine();
                    Book foundBook = bookshop.searchBookByISBN(searchISBN);
                    if (foundBook != null) {
                        System.out.println("Libro trovato: " + foundBook.getTitle() + " di " + foundBook.getAuthor());
                    } else {
                        System.out.println("Libro non trovato.");
                    }
                    break;
                case 3:
                    System.out.print("ISBN: ");
                    String borrowISBN = scanner.nextLine();
                    if (bookshop.giveBook(borrowISBN)) {
                        System.out.println("Libro preso in prestito con successo.");
                    } else {
                        System.out.println("Il libro non è disponibile.");
                    }
                    break;
                case 4:
                    System.out.print("ISBN: ");
                    String returnISBN = scanner.nextLine();
                    if (bookshop.returnBook(returnISBN)) {
                        System.out.println("Libro restituito con successo.");
                    } else {
                        System.out.println("Il libro non è stato preso in prestito.");
                    }
                    break;
                case 5:
                    exit = true;
                    System.out.println("Uscita dal programma.");
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        }

        scanner.close();
    }
}