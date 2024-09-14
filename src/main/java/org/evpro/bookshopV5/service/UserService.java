package org.evpro.bookshopV5.service;

import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.Cart;
import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.service.functions.UserFunctions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserFunctions {


    @Override
    public User getUserById(Integer userId) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public void updateUserProfile(Integer userId, User updatedUser) {

    }

    @Override
    public void changeUserPassword(Integer userId, String oldPassword, String newPassword) {

    }

    @Override
    public List<Loan> getUserLoanHistory(Integer userId) {
        return List.of();
    }

    @Override
    public Cart getUserCart(Integer userId) {
        return null;
    }

    @Override
    public User addNewUser(User user) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public void updateUserRole(Integer userId, RoleCode newRole) {

    }

    @Override
    public void deactivateUser(Integer userId) {

    }

    @Override
    public void reactivateUser(Integer userId) {

    }

    @Override
    public long getTotalUserCount() {
        return 0;
    }

    @Override
    public List<User> getMostActiveUsers(int limit) {
        return List.of();
    }

    @Override
    public List<User> getUsersWithOverdueLoans() {
        return List.of();
    }

    @Override
    public void deleteUser(Integer userId) {

    }

    @Override
    public void resetUserPassword(Integer userId) {

    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        return List.of();
    }
}
