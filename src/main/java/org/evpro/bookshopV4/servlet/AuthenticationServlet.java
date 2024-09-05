package org.evpro.bookshopV4.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.implementation.UserDAOImplementation;
import org.evpro.bookshopV4.exception.UserException;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.UserRole;
import org.evpro.bookshopV4.model.request.LoginRequest;
import org.evpro.bookshopV4.model.request.SignupRequest;
import org.evpro.bookshopV4.service.AuthenticationService;
import org.evpro.bookshopV4.service.UserService;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequireRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.evpro.bookshopV4.utilities.CodeMsg.AJ_FORMAT;

@Slf4j
@WebServlet("/authentication/*")
public class AuthenticationServlet extends BaseServlet {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public AuthenticationServlet() {
        this(new AuthenticationService(new UserDAOImplementation(),
             new UserService(new UserDAOImplementation())),
             new UserService(new UserDAOImplementation()),
             new ObjectMapper());
    }
    public AuthenticationServlet(AuthenticationService authenticationService, UserService userService, ObjectMapper objectMapper) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @HandlerMapping(path = "/login", method = "POST")
    public void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
        try {
            if (authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword())) {
                User user = userService.getUserByEmail(loginRequest.getEmail());
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            log.error("Database error during login", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @HandlerMapping(path = "/signup", method = "POST")
    public void handleSignup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SignupRequest signupRequest = objectMapper.readValue(request.getReader(), SignupRequest.class);
        User user = userInitialization(signupRequest);

        try {
            if (authenticationService.signup(user)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType(AJ_FORMAT);
                response.getWriter().write(objectMapper.writeValueAsString(user));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("User already exists");
            }
        } catch (UserException e) {
            response.setStatus(e.getHttpStatus().getCode());
            response.getWriter().write(e.getMessage());
        } catch (SQLException e) {
            log.error("Database error during registration", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An internal error occurred");
        }
    }

    @HandlerMapping(path = "/logout", method = "POST")
    public void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private User userInitialization(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getName());
        user.setSurname(signupRequest.getSurname());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDate.now());
        return user;
    }

}
