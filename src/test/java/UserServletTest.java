import com.fasterxml.jackson.databind.ObjectMapper;
import org.evpro.bookshopV4.exception.UserException;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.model.enums.UserRole;
import org.evpro.bookshopV4.service.UserService;
import org.evpro.bookshopV4.servlet.UserServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.evpro.bookshopV4.utilities.CodeMsg.AJ_FORMAT;
import static org.evpro.bookshopV4.utilities.CodeMsg.NUP_CODE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServletTest {

    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private UserServlet userServlet;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        userServlet = new UserServlet(userService, objectMapper);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testAddUser() throws Exception {
        User user = initializeUser(1);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(user))));
        when(userService.addUserAdmin(any(User.class))).thenReturn(user);

        userServlet.handleAddUserAdmin(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(userService).addUserAdmin(any(User.class));
        verify(response).setContentType(AJ_FORMAT);

        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains(user.getName()), "Response should contain name");
        assertTrue(responseContent.contains(user.getSurname()), "Response should contain surname");
        assertTrue(responseContent.contains(user.getEmail()), "Response should contain email");
    }

    @Test
    void testAddExistingUser() throws Exception {
        User existingUser = initializeUser(1);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(existingUser))));
        when(userService.addUserAdmin(any(User.class))).thenReturn(null);

        userServlet.handleAddUserAdmin(request, response);

        verify(response).sendError(HttpServletResponse.SC_CONFLICT, "User already exists");
        verify(userService).addUserAdmin(any(User.class));
    }

    @Test
    void testAddNullUser() throws Exception {
        User nullUser = new User();
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(nullUser))));

        userServlet.handleAddUserAdmin(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, NUP_CODE);
    }

    @Test
    void testAddEmptyInput() throws Exception {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));

        userServlet.handleAddUserAdmin(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, NUP_CODE);
    }

    @Test
    void testGetUser() throws Exception {
        User user = initializeUser(1);
        when(request.getParameter("search-type")).thenReturn("id");
        when(request.getParameter("search-value")).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);

        userServlet.handleGetUser(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains(user.getEmail()), "Response should contain email");
    }

    @Test
    void testGetUserWithWrongId() throws Exception {
        when(request.getParameter("search-type")).thenReturn("id");
        when(request.getParameter("search-value")).thenReturn("999");
        when(userService.getUserById(999)).thenThrow(new UserException("User not found", HttpStatusCode.NOT_FOUND));

        userServlet.handleGetUser(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
    }

    @Test
    void testGetAllUser() throws Exception {
        List<User> users = Arrays.asList(initializeUser(1), initializeUser(2));
        when(userService.getAllUsers()).thenReturn(users);

        userServlet.handleGetAllUsers(response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains(users.get(0).getEmail()), "Response should contain first user's email");
        assertTrue(responseContent.contains(users.get(1).getEmail()), "Response should contain second user's email");
    }

    @Test
    void testUpdateUserRole() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(userService.updateUserRole(1, UserRole.ADMIN)).thenReturn(true);

        userServlet.handleUpdateUserRole(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("User role updated successfully"), "Response should confirm role update");
    }

    @Test
    void testUpdateUserRoleFailed() throws Exception {
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(userService.updateUserRole(999, UserRole.ADMIN)).thenReturn(false);

        userServlet.handleUpdateUserRole(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Any user found ");
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = initializeUser(1);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(user))));
        when(userService.updateUser(any(User.class))).thenReturn(true);

        userServlet.handleUpdateUser(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("User updated successfully"), "Response should confirm user update");
    }

    @Test
    void testChangeInfoOfUser() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("NewName");
        when(request.getParameter("surname")).thenReturn("NewSurname");
        when(userService.changePersonalInfo(1, "NewName", "NewSurname")).thenReturn(true);

        userServlet.handleUpdateUserInfo(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("User info updated successfully"), "Response should confirm info update");
    }

    @Test
    void testChangeInfoOfUserWithInvalidParameters() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("surname")).thenReturn("NewSurname");

        userServlet.handleUpdateUserInfo(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter: name");
    }

    @Test
    void testDeleteUser() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(userService.deleteUser(1)).thenReturn(true);

        userServlet.handleDeleteUser(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("User deleted successfully"), "Response should confirm user deletion");
    }

    @Test
    void testDeleteUserNotFounded() throws Exception {
        when(request.getParameter("id")).thenReturn("999");
        when(userService.deleteUser(999)).thenReturn(false);

        userServlet.handleDeleteUser(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Any user found ");
    }

    @Test
    void testDeleteAll() throws Exception {
        when(userService.deleteAllUsers()).thenReturn(true);

        userServlet.handleDeleteAll(response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType(AJ_FORMAT);
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("All users deleted"), "Response should confirm all users deletion");
    }

    private User initializeUser(int id) {
        User user = new User();
        user.setId(id);
        user.setName("Admin");
        user.setSurname("Test");
        user.setPassword("Psw123");
        user.setEmail("admin.test@example.com");
        user.setCreatedAt(LocalDate.now());
        return user;
    }
}