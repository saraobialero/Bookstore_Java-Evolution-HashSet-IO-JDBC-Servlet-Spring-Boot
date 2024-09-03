package org.evpro.bookshopV4.servlet;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequireRole;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@WebServlet("/loans")
public class UserHasBookServlet {

    //both?
    @RequireRole("USER")
    @HandlerMapping(path = "/books", method = "GET")
    protected void handleGetBooksForUser(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("USER")
    @HandlerMapping(path = "/borrow", method = "POST")
    protected void handleBorrowBook(HttpServletRequest request, HttpServletResponse response) {
    }

    //BOTH
    @RequireRole("USER")
    @HandlerMapping(path = "/borrow/user", method = "GET")
    protected void handleGetBorrowForUser(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/borrow/book", method = "GET")
    protected void handleGetBorrowForBook(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/borrow/return", method = "PUT")
    protected void handleReturnBorrow(HttpServletRequest request, HttpServletResponse response) {
    }

}
