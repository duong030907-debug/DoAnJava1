package com.tdc.nhom6.doanjava1.service.impl;
import com.tdc.nhom6.doanjava1.dao.BookDao;
import com.tdc.nhom6.doanjava1.dao.BorrowRecordDao;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.Book;
import com.tdc.nhom6.doanjava1.entity.BorrowRecord;
import com.tdc.nhom6.doanjava1.dao.AccountDao;
import com.tdc.nhom6.doanjava1.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class BorrowServiceImpl implements BorrowService {

    @Autowired private BorrowRecordDao borrowDao;
    @Autowired private BookDao bookDao;
    @Autowired private AccountDao accountDao;

    private static final int BORROW_DAYS   = 14;
    private static final BigDecimal FINE_RATE = new BigDecimal("2000");

    @Override
    public BorrowRecord requestBorrow(Long accountId, Long bookId, String notes) {
        Account account = accountDao.findById(accountId);
        Book book = bookDao.findById(bookId);
        if (account == null) throw new RuntimeException("Không tìm thấy tài khoản");
        if (book == null)    throw new RuntimeException("Không tìm thấy sách");
        if (book.getAvailableCopies() <= 0) throw new RuntimeException("Sách hiện không còn bản để mượn");
        if (borrowDao.existsByAccountAndBookAndStatus(account, book, "ACTIVE"))
            throw new RuntimeException("Bạn đang mượn cuốn sách này rồi");
        LocalDate today = LocalDate.now();
        BorrowRecord r = new BorrowRecord();
        r.setAccount(account);
        r.setBook(book);
        r.setBorrowDate(today);
        r.setDueDate(today.plusDays(BORROW_DAYS));
        r.setStatus("ACTIVE");
        r.setFineAmount(BigDecimal.ZERO);
        r.setNotes(notes);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.save(book);
        return borrowDao.save(r);
    }

    @Override
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord r = borrowDao.findById(recordId);
        if (r == null) throw new RuntimeException("Không tìm thấy phiếu mượn");
        if (!"ACTIVE".equals(r.getStatus())) throw new RuntimeException("Phiếu mượn này đã được đóng");
        LocalDate today = LocalDate.now();
        r.setReturnDate(today);
        if (today.isAfter(r.getDueDate())) {
            long days = ChronoUnit.DAYS.between(r.getDueDate(), today);
            r.setFineAmount(FINE_RATE.multiply(BigDecimal.valueOf(days)));
            r.setStatus("RETURNED_LATE");
            r.setFinePaid(false);
        } else {
            r.setFineAmount(BigDecimal.ZERO);
            r.setStatus("RETURNED");
            r.setFinePaid(true);
        }
        r.getBook().setAvailableCopies(r.getBook().getAvailableCopies() + 1);
        bookDao.save(r.getBook());
        return borrowDao.save(r);
    }

    @Override
    public BorrowRecord payFine(Long recordId) {
        BorrowRecord r = borrowDao.findById(recordId);
        if (r == null) throw new RuntimeException("Không tìm thấy phiếu mượn");
        if (r.isFinePaid()) throw new RuntimeException("Tiền phạt đã được thanh toán");
        if (r.getFineAmount().compareTo(BigDecimal.ZERO) == 0) throw new RuntimeException("Không có tiền phạt");
        r.setFinePaid(true);
        return borrowDao.save(r);
    }

    @Override public BorrowRecord findById(Long id)        { return borrowDao.findById(id); }
    @Override public List<BorrowRecord> findActive()       { return borrowDao.findByStatus("ACTIVE"); }
    @Override public List<BorrowRecord> findActiveByAccount(Long accountId) {
        Account a = accountDao.findById(accountId);
        return a != null ? borrowDao.findByAccountAndStatus(a, "ACTIVE") : java.util.Collections.emptyList();
    }
    @Override public List<BorrowRecord> findByAccount(Long accountId) {
        Account a = accountDao.findById(accountId);
        return a != null ? borrowDao.findByAccount(a) : java.util.Collections.emptyList();
    }
    @Override public List<BorrowRecord> findOverdue()      { return borrowDao.findOverdue(LocalDate.now()); }

    @Override
    public java.util.Set<Long> findActiveBookIds(Long accountId) {
        return new java.util.HashSet<>(borrowDao.findActiveBookIdsByAccount(accountId));
    }
}
