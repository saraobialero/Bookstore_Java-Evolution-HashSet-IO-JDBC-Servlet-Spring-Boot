package org.evpro.bookshopV1;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Bookshop {
    private Set<Book> books = new HashSet<>();

    public boolean addBook(Book book) {
        return books.add(book);
    }
    public boolean giveBook(String ISBN) {
        if (!isAvailable(ISBN)) {
            throw new BookException("Book is not available");
        }
        searchBookByISBN(ISBN).setAvailable(false);
        return true;
    }
    public boolean returnBook(String ISBN) {
        if (isAvailable(ISBN)) {
            throw new BookException("Book is already available");
        }
        searchBookByISBN(ISBN).setAvailable(true);
        return true;
    }
    public Book searchBookByISBN(String ISBN) {
        for (Book book : books) {
            if (book.getISBN().equals(ISBN)) {
                return book;
            }
        }
        throw new BookException("Book with ISBN " + ISBN + " not found");
    }
    private boolean isAvailable (String ISBN) {
        return searchBookByISBN(ISBN).isAvailable();
    }

}
