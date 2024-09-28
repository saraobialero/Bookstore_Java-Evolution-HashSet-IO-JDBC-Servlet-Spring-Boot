package org.evpro.bookshopV5.service;


import jakarta.servlet.http.HttpServletRequest;
import org.evpro.bookshopV5.model.DTO.response.UserDTO;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public CustomUserDetailsService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(
                new ErrorResponse(ErrorCode.EUN, "User not found with email: " + email)
        ));
    }

    public UserDTO loadUser(HttpServletRequest request) {
        User user = getUserFromToken(request);
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .build();
    }

    public User getUserFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserException(new ErrorResponse(ErrorCode.BR, "missing or invalid Bearer token"));
        }

        String token = authHeader.substring(7);
        String email = jwtUtils.extractEmail(token);
        //Get user from token
        UserDetails userDetails = this.loadUserByUsername(email);


        if(!jwtUtils.isTokenValid(token, userDetails)) {
            throw new UserException(new ErrorResponse(ErrorCode.BR, "Token is not valid or expired"));
        }
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(new ErrorResponse(ErrorCode.EUN, "User not found")));
    }
}
