package org.evpro.bookshopV4.servlet;

import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequireRole;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/authentication")
public class AuthenticationServlet {

    @RequireRole("USER")
    @HandlerMapping(path = "/login", method = "POST")
    protected void handleLoginUser(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/login", method = "POST")
    protected void handleLoginAdmin(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("USER")
    @HandlerMapping(path = "/signup", method = "POST")
    protected void handleSignup(HttpServletRequest request, HttpServletResponse response) {
    }
}
