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

    //Methods
    //Add book to the bookshop
    public boolean addBook(Book book) {
        return books.add(book);
    }

    //GiveBook
    public boolean giveBook(String ISBN) {
        if (!isAvailable(ISBN)) {
            return false;
        }
        searchBookByISBN(ISBN).setAvailable(false);
        return true;
    }

    //ReturnBook
    public boolean returnBook(String ISBN) {
        if (isAvailable(ISBN)) {
            return false;
        }
        searchBookByISBN(ISBN).setAvailable(true);
        return true;
    }


    //Found book by ISBN
    public Book searchBookByISBN(String ISBN) {
        for (Book book: books) {
            if (book.getISBN().equals(ISBN)) {
                return book;
            }
        }
        return null;
    }

    //verify if the book is available
    private boolean isAvailable (String ISBN) {
        return searchBookByISBN(ISBN).isAvailable();
    }

}
