package org.evpro.bookshopV5.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.evpro.bookshopV5.data.DTO.AuthenticationResponse;
import org.evpro.bookshopV5.data.request.AuthRequest;
import org.evpro.bookshopV5.data.request.SignupRequest;
import org.evpro.bookshopV5.data.response.SuccessResponse;
import org.evpro.bookshopV5.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookshop/v5/authentication")
public class AuthenticationController {


    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthenticationResponse>> login(@RequestBody AuthRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(authenticationService.authentication(request)), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<AuthenticationResponse>> signup(@RequestBody SignupRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(authenticationService.registration(request)), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<SuccessResponse<AuthenticationResponse>> refreshToken(HttpServletRequest request)  {
        return new ResponseEntity<>(new SuccessResponse<>(authenticationService.refreshToken(request)), HttpStatus.OK);
    }

}
