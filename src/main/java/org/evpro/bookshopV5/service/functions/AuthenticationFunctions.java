package org.evpro.bookshopV5.service.functions;

import jakarta.servlet.http.HttpServletRequest;
import org.evpro.bookshopV5.data.DTO.AuthenticationResponse;
import org.evpro.bookshopV5.data.request.AuthRequest;
import org.evpro.bookshopV5.data.request.SignupRequest;

public interface AuthenticationFunctions {
    AuthenticationResponse authentication(AuthRequest request);
    AuthenticationResponse refreshToken(HttpServletRequest request);
    AuthenticationResponse registration(SignupRequest request);
}
