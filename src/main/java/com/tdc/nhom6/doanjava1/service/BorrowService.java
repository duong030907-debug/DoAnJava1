package com.tdc.nhom6.doanjava1.service;
import com.tdc.nhom6.doanjava1.entity.BorrowRecord;
import java.util.List;
public interface BorrowService {
    BorrowRecord requestBorrow(Long accountId, Long bookId, String notes);
    BorrowRecord returnBook(Long recordId);
    BorrowRecord payFine(Long recordId);
    BorrowRecord findById(Long id);
    List<BorrowRecord> findActive();
    List<BorrowRecord> findActiveByAccount(Long accountId);
    List<BorrowRecord> findByAccount(Long accountId);
    List<BorrowRecord> findOverdue();
    /** Tập ID sách mà người dùng đang mượn — dùng cho badge "Đang mượn" ở trang chủ. */
    java.util.Set<Long> findActiveBookIds(Long accountId);
}
