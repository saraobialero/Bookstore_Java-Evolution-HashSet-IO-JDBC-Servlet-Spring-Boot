package org.evpro.bookshopV5.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.data.DTO.AuthenticationResponse;
import org.evpro.bookshopV5.data.request.AuthRequest;
import org.evpro.bookshopV5.data.response.ErrorResponse;
import org.evpro.bookshopV5.exception.AuthException;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.repository.RoleRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {


}
