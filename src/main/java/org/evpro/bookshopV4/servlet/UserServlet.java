package org.evpro.bookshopV4.servlet;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequireRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    //TODO: Implements service here

    @RequireRole("ADMIN")
    @HandlerMapping(path = "user/add", method = "POST")
    protected void handleAddUserAdmin(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "user/delete", method = "DELETE")
    protected void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/delete/all", method = "DELETE")
    protected void handleDeleteAll(HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/user", method = "GET")
    protected void handleGetUser(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/user", method = "GET")
    protected void handleGetUserByEmail(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/user", method = "GET")
    protected void handleGetUserByRole(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/all", method = "GET")
    protected void handleGetAllUsers(HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "user/update/role", method = "PUT")
    protected void handleUpdateUserRole(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "user/update", method = "PUT")
    protected void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("USER")
    @HandlerMapping(path = "user/update/info", method = "PUT")
    protected void handleUpdateUserInfo(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("USER")
    @HandlerMapping(path = "user/update/pass", method = "PUT")
    protected void handleUpdateUserPassword(HttpServletRequest request, HttpServletResponse response) {
    }

    @RequireRole("USER")
    @HandlerMapping(path = "user/update/emal", method = "PUT")
    protected void handleUpdateUserEmail(HttpServletRequest request, HttpServletResponse response) {
    }


}
