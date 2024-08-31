package org.evpro.bookshopV4.servlet;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.service.BookService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

@Slf4j
@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private BookService bookService;

    public BookServlet(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            switch (pathInfo) {
                case "/add-book":
                    handleAddBook(request, response);
                    break;
                case "/add-books":
                    handleAddBooks(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation not found");
            }
        } catch (Exception e) {
            log.error("Unexpected error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        }
    }


    private void handleAddBook(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String description = request.getParameter("description");
        Date publicationYear = Date.valueOf(request.getParameter("publication_year"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        boolean available = Boolean.parseBoolean(request.getParameter("available"));

        Book book = initializeBook(title, author, isbn, description, publicationYear, quantity, available);

        try {
            boolean bookAdded = bookService.addBook(book);
            if (bookAdded) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                log.info("Book added: {}", book);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book exists, updated quantity: {}", book);
            }
        } catch (SQLException e) {
            log.error("Error adding book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleAddBooks(HttpServletRequest request, HttpServletResponse response) {
    }


    private Book initializeBook(String title, String author, String isbn, String description, Date publicationYear, int quantity, boolean available) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setISBN(isbn);
        book.setDescription(description);
        book.setQuantity(quantity);
        book.setAvailable(available);
        book.setPublicationYear(publicationYear);
        return book;

    }



}
