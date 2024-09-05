package org.evpro.bookshopV4.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.exception.BadRequestException;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.exception.UserException;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.model.enums.UserRole;
import org.evpro.bookshopV4.service.UserService;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequestParameterExtractor;
import org.evpro.bookshopV4.utilities.RequireRole;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.evpro.bookshopV4.utilities.CodeMsg.*;

@Slf4j
@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserServlet() {
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    public UserServlet(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/user/add", method = "POST")
    public void handleAddUserAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = parseAndValidateUserInput(request, response);
            if (user == null) {
                return;
            }
            User userAdded = addUserToSystem(user, response);
            if (userAdded != null) {
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, userAdded);
            }
        } catch (JsonMappingException | JsonParseException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No user provided or deserialization failed");
        } catch (SQLException e) {
            log.error(EAU_CODE);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, EAU_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/user/filter", method = "GET")
    public void handleGetUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String searchType = RequestParameterExtractor.extractStringParameter(request, "search-type");
            String searchValue = RequestParameterExtractor.extractStringParameter(request, "search-value");
            User user;

            switch (searchType.toLowerCase()) {
                case "id":
                    user = userService.getUserById(Integer.parseInt(searchValue));
                    break;
                case "email":
                    user = userService.getUserByEmail(searchValue);
                    break;
                default:
                    throw new BadRequestException("Invalid search type", HttpStatusCode.BAD_REQUEST);
            }
            sendJsonResponse(response, HttpServletResponse.SC_OK, user);
        } catch (BadRequestException | UserException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException | IOException e) {
            log.error(DB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/filter/role", method = "GET")
    public void handleGetUsersByRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserRole role = RequestParameterExtractor.extractEnumParameter(request, "role", UserRole.class);
            List<User> usersByRole = userService.getUserByRole(role);
            sendJsonResponse(response, HttpServletResponse.SC_OK, usersByRole);
        } catch (UserException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException | IOException e) {
            log.error(EVU_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/all", method = "GET")
    public void handleGetAllUsers(HttpServletResponse response) throws IOException {
        try {
            List<User> users = userService.getAllUsers();
            sendJsonResponse(response, HttpServletResponse.SC_OK, users);
        } catch (BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVU_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "user/update/role", method = "PUT")
    public void handleUpdateUserRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            UserRole role = RequestParameterExtractor.extractEnumParameter(request, "role", UserRole.class);
            boolean roleUpdated = userService.updateUserRole(id, role);
            if (roleUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "User role updated successfully");
            }
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, AUF_CODE);
        } catch (BadRequestException | UserException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error updating user role", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "user/update", method = "PUT")
    public void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = objectMapper.readValue(request.getReader(), User.class);
            boolean userUpdated = userService.updateUser(user);
            if (userUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "User updated successfully");
            }
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to update user");
        } catch (SQLException e) {
            log.error("Error updating user", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("USER")
    @HandlerMapping(path = "user/update/info", method = "PUT")
    public void handleUpdateUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            String name = RequestParameterExtractor.extractStringParameter(request, "name");
            String surname = RequestParameterExtractor.extractStringParameter(request, "surname");
            boolean roleUpdated = userService.changePersonalInfo(id, name, surname);
            if (roleUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "User info updated successfully");
            }

            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, AUF_CODE);
        } catch (BadRequestException | UserException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error updating user info", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/user/delete", method = "DELETE")
    public void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            boolean userDeleted = userService.deleteUser(id);
            if (userDeleted) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "User deleted successfully");
            }
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, AUF_CODE);
        } catch (BadRequestException | UserException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVU_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/delete/all", method = "DELETE")
    public void handleDeleteAll(HttpServletResponse response) throws SQLException, IOException {
        try {
            boolean usersDeleted = userService.deleteAllUsers();
            if (usersDeleted) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "All users deleted");
            }
            sendErrorResponse(response, HttpServletResponse.SC_NO_CONTENT, AUF_CODE);
        } catch (UserException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVU_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }



    private void sendJsonResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setContentType(AJ_FORMAT);
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), data);
    }
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }
    private boolean isUserEmpty(User user) {
        return user.getName() == null && user.getSurname() == null && user.getEmail() == null;
    }
    private User parseAndValidateUserInput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userJson = request.getReader().lines().collect(Collectors.joining());
        if (userJson.trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, NUP_CODE);
            return null;
        }

        User user = objectMapper.readValue(userJson, User.class);
        if (isUserEmpty(user)) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, NUP_CODE);
            return null;
        }

        return user;
    }
    private User addUserToSystem(User user, HttpServletResponse response) throws SQLException, IOException {
        User userAdded = userService.addUserAdmin(user);
        if (userAdded == null) {
            log.error("User {} already exists", user.getEmail());
            sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "User already exists");
        }
        return userAdded;
    }

}
