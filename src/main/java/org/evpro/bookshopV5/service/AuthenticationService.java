package org.evpro.bookshopV5.service;

import jakarta.servlet.http.HttpServletRequest;
import org.evpro.bookshopV5.model.DTO.response.AuthenticationResponse;
import org.evpro.bookshopV5.model.DTO.request.LoginRequest;
import org.evpro.bookshopV5.model.DTO.request.SignupRequest;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.exception.AuthException;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.RoleRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.functions.AuthenticationFunctions;
import org.evpro.bookshopV5.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService implements AuthenticationFunctions {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public AuthenticationResponse authentication(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(new ErrorResponse(ErrorCode.EUN, "user with email not found")));
        return AuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(user))
                .refreshToken(jwtUtils.generateRefreshToken(user))
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        User user = customUserDetailsService.getUserFromToken(request);
        return AuthenticationResponse.builder().accessToken(jwtUtils.generateToken(user))
                                               .refreshToken(jwtUtils.generateRefreshToken(user))
                                               .build();
    }

    @Override
    public AuthenticationResponse registration(SignupRequest request) {
        // Validate email
        if (!isValidEmail(request.getEmail())) {
            throw new AuthException(new ErrorResponse(ErrorCode.IVE, "Invalid email format"));
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException(new ErrorResponse(ErrorCode.EAE, "Email already exists"));
        }

        // Validate password
        if (!isValidPassword(request.getPassword())) {
            throw new AuthException(new ErrorResponse(ErrorCode.IVP, "Invalid password. It must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"));
        }

        // Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthException(new ErrorResponse(ErrorCode.PWM, "Passwords do not match"));
        }

        // Validate name and surname
        if (!isValidName(request.getName()) || !isValidName(request.getSurname())) {
            throw new AuthException(new ErrorResponse(ErrorCode.IVD, "Invalid name or surname. They should contain only letters and be between 2 and 50 characters long"));
        }

        // Encode the password before saving
        String encodedPsw = passwordEncoder.encode(request.getPassword());
        Role roleUser = roleRepository.findByRoleCode(RoleCode.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));

        // Save the new user
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPsw)
                .name(request.getName())
                .surname(request.getSurname())
                .roles(List.of(roleUser))
                .build();
        userRepository.save(user);

        // Generate tokens directly using JwtUtils
        return AuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(user))
                .refreshToken(jwtUtils.generateRefreshToken(user))
                .build();
    }

    // Helper methods for validation
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        // Corrected regex to properly validate the password
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=\\S+$).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    private boolean isValidName(String name) {
        String nameRegex = "^[A-Za-z]{2,50}$";
        return name != null && name.matches(nameRegex);
    }
}
