package org.evpro.bookshopV5.service.functions;

import jakarta.servlet.http.HttpServletRequest;
import org.evpro.bookshopV5.model.DTO.response.AuthenticationResponse;
import org.evpro.bookshopV5.model.DTO.request.LoginRequest;
import org.evpro.bookshopV5.model.DTO.request.SignupRequest;

public interface AuthenticationFunctions {
    AuthenticationResponse authentication(LoginRequest request);
    AuthenticationResponse refreshToken(HttpServletRequest request);
    AuthenticationResponse registration(SignupRequest request);
}
