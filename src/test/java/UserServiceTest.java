import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.DTO.request.AddUserRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateRoleRequest;
import org.evpro.bookshopV5.model.DTO.response.UserDTO;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.RoleRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_Success() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testUpdateUserProfile_Success() {
        String email = "john@example.com";
        String newName = "John Updated";
        String newSurname = "Doe Updated";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setName("John");
        existingUser.setSurname("Doe");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateUserProfile(email, newName, newSurname);

        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newSurname, result.getSurname());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testChangeEmail_Success() {
        String oldEmail = "old@example.com";
        String password = "password";
        String newEmail = "new@example.com";

        User user = new User();
        user.setEmail(oldEmail);
        user.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(oldEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.changeEmail(oldEmail, password, newEmail);

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testAddNewUser_Success() {
        AddUserRequest request = new AddUserRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setRoleCodes(Collections.singletonList(RoleCode.ROLE_USER));

        Role userRole = new Role();
        userRole.setRole(RoleCode.ROLE_USER);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleCode(RoleCode.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1);
            return savedUser;
        });

        UserDTO result = userService.addNewUser(request);

        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserRole_Success() {
        Integer userId = 1;
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setRoleCodes(Arrays.asList(RoleCode.ROLE_USER, RoleCode.ROLE_ADMIN));

        User user = new User();
        user.setId(userId);
        user.setRoles(new ArrayList<>());

        Role userRole = new Role();
        userRole.setRole(RoleCode.ROLE_USER);
        Role adminRole = new Role();
        adminRole.setRole(RoleCode.ROLE_ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleCode(RoleCode.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByRoleCode(RoleCode.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateUserRole(userId, request);

        assertNotNull(result);
        assertEquals(2, result.getRoles().size());
        verify(userRepository).save(user);
    }

    @Test
    void testGetMostActiveUsers_Success() {
        int limit = 5;
        List<User> activeUsers = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            User user = new User();
            user.setId(i + 1);
            user.setName("User " + (i + 1));
            activeUsers.add(user);
        }

        when(userRepository.findMostActiveUsers(any())).thenReturn(activeUsers);

        Set<UserDTO> result = userService.getMostActiveUsers(limit);

        assertEquals(limit, result.size());
    }

}