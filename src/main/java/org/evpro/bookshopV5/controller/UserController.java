package org.evpro.bookshopV5.controller;

import jakarta.validation.Valid;
import org.evpro.bookshopV5.model.DTO.request.*;
import org.evpro.bookshopV5.model.DTO.response.SuccessResponse;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.service.BookService;
import org.evpro.bookshopV5.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookshop/v5/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getUserById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUserById(id)), HttpStatus.OK);
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getUserByEmail(@PathVariable("email") String email) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUserByEmail(email)), HttpStatus.OK);
    }

    @PutMapping("/user/{id}/new-info")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateUserInfo(@PathVariable("id") Integer id, @RequestBody @Valid UpdateProfileRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.updateUserProfile(id, request.getNewName(), request.getNewSurname())), HttpStatus.OK);
    }

    @PatchMapping("/user/{id}/new-email")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateUserEmail(@PathVariable("id") Integer id, @RequestBody @Valid UpdateEmailRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.changeEmail(id, request.getPassword(), request.getNewEmail())), HttpStatus.OK);
    }

    @PatchMapping("/user/{id}/new-password")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateUserPsw(@PathVariable("id") Integer id, @RequestBody @Valid UpdatePasswordRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.changeUserPassword(id, request.getOldPassword(), request.getNewPassword(), request.getConfirmNewPassword())), HttpStatus.OK);
    }

    @GetMapping("/user/{id}/loan-history")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getUserLoanHistory(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUserLoanHistory(id)), HttpStatus.OK);
    }

    @GetMapping("/user/{id}/cart")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getUserCart(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUserCart(id)), HttpStatus.OK);
    }


    @PostMapping("/user/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> addUser(@RequestBody @Valid AddUserRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.addNewUser(request)), HttpStatus.OK);
    }

    @PostMapping("/user/add-multiple")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> addUsers(@RequestBody @Valid List<AddUserRequest> requests) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.addNewUsers(requests)), HttpStatus.OK);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getAllUsers() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getAllUsers()), HttpStatus.OK);
    }

    @PatchMapping("/user/{id}/update-role/{role}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateUserRole(@PathVariable ("id") Integer id, @RequestBody @Valid UpdateRoleRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.updateUserRole(id, request)), HttpStatus.OK);
    }

    @PatchMapping("/user/{id}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deactivateUser(@PathVariable ("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.deactivateUser(id)), HttpStatus.OK);
    }

    @PatchMapping("/user/{id}/reactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> reactivateUser(@PathVariable ("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.reactivateUser(id)), HttpStatus.OK);
    }

    @PatchMapping("/user/{id}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> resetUserPsw(@PathVariable ("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.resetUserPassword(id)), HttpStatus.OK);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getTotalUserCount() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getTotalUserCount()), HttpStatus.OK);
    }

    @GetMapping("/most-active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getMostActiveUsers(@RequestParam int limit) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getMostActiveUsers(limit)), HttpStatus.OK);
    }

    @GetMapping("/loans/overdue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getUsersWithOverdueLoans() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.getUsersWithOverdueLoans()), HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deleteUserById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(userService.deleteUser(id)), HttpStatus.OK);
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deleteAllUsers() {
        return new ResponseEntity<>(new SuccessResponse<>(userService.deleteAll()), HttpStatus.OK);
    }




}
