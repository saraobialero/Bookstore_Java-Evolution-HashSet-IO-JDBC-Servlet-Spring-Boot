import org.evpro.bookshopV5.exception.AuthException;
import org.evpro.bookshopV5.model.DTO.request.LoginRequest;
import org.evpro.bookshopV5.model.DTO.request.SignupRequest;
import org.evpro.bookshopV5.model.DTO.response.AuthenticationResponse;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.RoleRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.AuthenticationService;
import org.evpro.bookshopV5.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthentication_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(java.util.Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.authentication(loginRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthentication_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(java.util.Optional.empty());

        assertThrows(AuthException.class, () -> authenticationService.authentication(loginRequest));
    }

    @Test
    void testRegistration_Success() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("Password123!");
        signupRequest.setName("John");
        signupRequest.setSurname("Doe");

        Role userRole = new Role();
        userRole.setRole(RoleCode.ROLE_USER);

        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(java.util.Optional.empty());
        when(roleRepository.findByRoleCode(RoleCode.ROLE_USER)).thenReturn(java.util.Optional.of(userRole));
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtUtils.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.registration(signupRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegistration_EmailAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("Password123!");
        signupRequest.setName("John");
        signupRequest.setSurname("Doe");

        when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(java.util.Optional.of(new User()));

        assertThrows(AuthException.class, () -> authenticationService.registration(signupRequest));
    }

    @Test
    void testRegistration_PasswordMismatch() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("DifferentPassword123!");
        signupRequest.setName("John");
        signupRequest.setSurname("Doe");

        assertThrows(AuthException.class, () -> authenticationService.registration(signupRequest));
    }

    @Test
    void testRegistration_InvalidEmail() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("invalidemail");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("Password123!");
        signupRequest.setName("John");
        signupRequest.setSurname("Doe");

        assertThrows(AuthException.class, () -> authenticationService.registration(signupRequest));
    }

    @Test
    void testRegistration_InvalidPassword() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("weak");
        signupRequest.setConfirmPassword("weak");
        signupRequest.setName("John");
        signupRequest.setSurname("Doe");

        assertThrows(AuthException.class, () -> authenticationService.registration(signupRequest));
    }
}