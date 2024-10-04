package org.evpro.bookshopV5.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.*;
import org.evpro.bookshopV5.model.DTO.response.AuthenticationResponse;
import org.evpro.bookshopV5.model.DTO.response.SuccessResponse;
import org.evpro.bookshopV5.model.DTO.response.UserDTO;
import org.evpro.bookshopV5.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookshop/v5/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<UserDTO>> getPersonalDetails(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUserByEmail(userEmail)), HttpStatus.OK);
    }

    @PutMapping("/new-info")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<UserDTO>> updateUserInfo(@AuthenticationPrincipal String userEmail,
                                                                   @RequestBody @Valid UpdateProfileRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.updateUserProfile(userEmail, request.getNewName(), request.getNewSurname())), HttpStatus.OK);
    }

    @PatchMapping("/new-email")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<AuthenticationResponse>> updateUserEmail(@AuthenticationPrincipal String userEmail,
                                                                                   @RequestBody @Valid UpdateEmailRequest request) {
        AuthenticationResponse response = userService.changeEmail(userEmail, request.getPassword(), request.getNewEmail());
        return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
    }

    @PatchMapping("/new-password")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> updateMyPSW(@AuthenticationPrincipal String userEmail, @RequestBody @Valid UpdatePasswordRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.changeUserPassword(userEmail, request.getOldPassword(), request.getNewPassword(), request.getConfirmNewPassword())), HttpStatus.OK);
    }


    // Admin functionality
    @GetMapping("/{id}/details")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<UserDTO>> getUserById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUserById(id)), HttpStatus.OK);
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<UserDTO>> addUser(@RequestBody @Valid AddUserRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.addNewUser(request)), HttpStatus.OK);
    }

    @PostMapping("/add-multiple")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<UserDTO>>> addUsers(@RequestBody @Valid List<AddUserRequest> requests) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.addNewUsers(requests)), HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<UserDTO>>> getAllUsers() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getAllUsers()), HttpStatus.OK);
    }

    @PatchMapping("/{id}/update-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<UserDTO>> updateUserRole(@PathVariable ("id") Integer id, @RequestBody UpdateRoleRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.updateUserRole(id, request)), HttpStatus.OK);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deactivateUser(@PathVariable ("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.deactivateUser(id)), HttpStatus.OK);
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> reactivateUser(@PathVariable ("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.reactivateUser(id)), HttpStatus.OK);
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<String>> resetUserPsw(@PathVariable ("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.resetUserPassword(id)), HttpStatus.OK);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Long>> getTotalUserCount() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getTotalUserCount()), HttpStatus.OK);
    }

    @GetMapping("/most-active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<UserDTO>>> getMostActiveUsers(@RequestParam int limit) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getMostActiveUsers(limit)), HttpStatus.OK);
    }

    @GetMapping("/loans/overdue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<UserDTO>>> getUsersWithOverdueLoans() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUsersWithOverdueLoans()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteUserById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.deleteUser(id)), HttpStatus.OK);
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteAllUsers() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.deleteAll()), HttpStatus.OK);
    }




}
