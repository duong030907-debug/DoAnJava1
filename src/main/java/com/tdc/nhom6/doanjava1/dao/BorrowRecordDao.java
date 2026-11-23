package com.tdc.nhom6.doanjava1.dao;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.Book;
import com.tdc.nhom6.doanjava1.entity.BorrowRecord;
import java.time.LocalDate;
import java.util.List;
public interface BorrowRecordDao {
    BorrowRecord save(BorrowRecord record);
    BorrowRecord findById(Long id);
    List<BorrowRecord> findAll();
    List<BorrowRecord> findByStatus(String status);
    List<BorrowRecord> findByAccount(Account account);
    List<BorrowRecord> findByAccountAndStatus(Account account, String status);
    List<BorrowRecord> findOverdue(LocalDate today);
    /** ID các cuốn sách mà tài khoản này đang mượn (status = ACTIVE). */
    List<Long> findActiveBookIdsByAccount(Long accountId);
    boolean existsByAccountAndBookAndStatus(Account account, Book book, String status);
    boolean existsByBook(Book book);
    long countBorrowsInPeriod(LocalDate start, LocalDate end);
    long countReturnsInPeriod(LocalDate start, LocalDate end);
    java.math.BigDecimal totalFinesCollected();
    long countOverdue(LocalDate today);
}
