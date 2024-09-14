package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findAllByTitle(String title);
    List<Book> findAllByAuthor(String author);
    List<Book> findByAvailableTrue();
    // @Query("SELECT b FROM Book b WHERE b.available = true")
    //  List<Book> findAllAvailableBooks();
    List<Book> findAllByGenre(BookGenre genre);

}
