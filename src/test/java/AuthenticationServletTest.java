import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.servlet.AuthenticationServlet;
import org.evpro.bookshopV4.service.AuthenticationService;
import org.evpro.bookshopV4.service.UserService;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.UserRole;
import org.evpro.bookshopV4.model.request.LoginRequest;
import org.evpro.bookshopV4.model.request.SignupRequest;
import org.evpro.bookshopV4.exception.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.sql.SQLException;

import static org.evpro.bookshopV4.model.enums.CodeAndFormat.AJ_FORMAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServletTest {

    @Mock private AuthenticationService authenticationService;
    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    private AuthenticationServlet servlet;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        servlet = new AuthenticationServlet(authenticationService, userService, objectMapper);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testHandleLoginSuccessful() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(loginRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.login(anyString(), anyString())).thenReturn(true);

        User user = new User(1, "Test", "User", "test@gmail.com", "hashedPassword", UserRole.USER, LocalDate.now());
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(request.getSession()).thenReturn(session);

        servlet.handleLogin(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(session).setAttribute("user", user);
    }

    @Test
    void testHandleLoginFailed() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", "wrongpassword");
        String jsonRequest = objectMapper.writeValueAsString(loginRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.login(anyString(), anyString())).thenReturn(false);

        servlet.handleLogin(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void testHandleLoginDatabaseError() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(loginRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.login(anyString(), anyString())).thenThrow(new SQLException("Database error"));

        servlet.handleLogin(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void testHandleSignupSuccessful() throws Exception {
        SignupRequest signupRequest = new SignupRequest("John", "Doe", "john@gmail.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(signupRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.signup(any(User.class))).thenReturn(true);

        servlet.handleSignup(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).setContentType(AJ_FORMAT);
    }

    @Test
    void testHandleSignupUserAlreadyExists() throws Exception {
        SignupRequest signupRequest = new SignupRequest("John", "Doe", "existing@gmail.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(signupRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.signup(any(User.class))).thenReturn(false);

        servlet.handleSignup(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("User already exists"));
    }

    @Test
    void testHandleSignupInvalidData() throws Exception {
        SignupRequest signupRequest = new SignupRequest("John", "Doe", "invalid-email", "weak");
        String jsonRequest = objectMapper.writeValueAsString(signupRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.signup(any(User.class))).thenThrow(new UserException("Invalid data", HttpStatusCode.BAD_REQUEST));

        servlet.handleSignup(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("Invalid data"));
    }

    @Test
    void testHandleSignupDatabaseError() throws Exception {
        SignupRequest signupRequest = new SignupRequest("John", "Doe", "john@gmail.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(signupRequest);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(authenticationService.signup(any(User.class))).thenThrow(new SQLException("Database error"));

        servlet.handleSignup(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(stringWriter.toString().contains("An internal error occurred"));
    }

    @Test
    void testHandleLogoutSuccessful() throws Exception {
        when(request.getSession(false)).thenReturn(session);

        servlet.handleLogout(request, response);

        verify(session).invalidate();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testHandleLogoutNoActiveSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.handleLogout(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}