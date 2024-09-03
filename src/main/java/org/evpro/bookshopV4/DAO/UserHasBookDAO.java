package org.evpro.bookshopV4.DAO;

import org.evpro.bookshopV4.model.UserHasBook;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserHasBookDAO {
    void save(UserHasBook userHasBook);
    void updateQuantity(int id, int quantity);
    void updateReturnDate(int id, LocalDate returnDate);
    Optional<UserHasBook> findById(int id);
    Optional<UserHasBook> findByUserIdAndBookId(int userId, int bookId);
    List<UserHasBook> findByUserId(int userId);
    List<UserHasBook> findByBookId(int bookId);
    List<UserHasBook> findByQuantity(int quantity);
    List<UserHasBook> findByRange(LocalDate startDate, LocalDate endDate);
    List<UserHasBook> findAll();
    List<UserHasBook> findReturned(LocalDate returnDate);
    void deleteById(int id);
    void deleteAll();

}
