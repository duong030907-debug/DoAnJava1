package com.tdc.nhom6.doanjava1.dao.impl;
import com.tdc.nhom6.doanjava1.dao.BorrowRecordDao;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.Book;
import com.tdc.nhom6.doanjava1.entity.BorrowRecord;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class BorrowRecordDaoImpl implements BorrowRecordDao {

    @PersistenceContext
    private EntityManager em;

    @Override public BorrowRecord save(BorrowRecord r)  { return em.merge(r); }
    @Override public BorrowRecord findById(Long id)     { return em.find(BorrowRecord.class, id); }

    @Override
    public List<BorrowRecord> findAll() {
        return em.createQuery("SELECT r FROM BorrowRecord r ORDER BY r.createdAt DESC", BorrowRecord.class).getResultList();
    }

    @Override
    public List<BorrowRecord> findByStatus(String status) {
        return em.createQuery("SELECT r FROM BorrowRecord r WHERE r.status = :s ORDER BY r.createdAt DESC", BorrowRecord.class)
                 .setParameter("s", status).getResultList();
    }

    @Override
    public List<BorrowRecord> findByAccount(Account account) {
        return em.createQuery("SELECT r FROM BorrowRecord r WHERE r.account = :a ORDER BY r.createdAt DESC", BorrowRecord.class)
                 .setParameter("a", account).getResultList();
    }

    @Override
    public List<BorrowRecord> findByAccountAndStatus(Account account, String status) {
        return em.createQuery("SELECT r FROM BorrowRecord r WHERE r.account = :a AND r.status = :s ORDER BY r.createdAt DESC", BorrowRecord.class)
                 .setParameter("a", account).setParameter("s", status).getResultList();
    }

    @Override
    public List<BorrowRecord> findOverdue(LocalDate today) {
        return em.createQuery("SELECT r FROM BorrowRecord r WHERE r.status = 'ACTIVE' AND r.dueDate < :t ORDER BY r.dueDate", BorrowRecord.class)
                 .setParameter("t", today).getResultList();
    }

    @Override
    public List<Long> findActiveBookIdsByAccount(Long accountId) {
        return em.createQuery(
            "SELECT r.book.id FROM BorrowRecord r WHERE r.account.id = :id AND r.status = 'ACTIVE'", Long.class)
            .setParameter("id", accountId).getResultList();
    }

    @Override
    public boolean existsByAccountAndBookAndStatus(Account account, Book book, String status) {
        Long c = em.createQuery("SELECT COUNT(r) FROM BorrowRecord r WHERE r.account=:a AND r.book=:b AND r.status=:s", Long.class)
                   .setParameter("a", account).setParameter("b", book).setParameter("s", status).getSingleResult();
        return c > 0;
    }

    @Override
    public boolean existsByBook(Book book) {
        Long c = em.createQuery("SELECT COUNT(r) FROM BorrowRecord r WHERE r.book = :b", Long.class)
                   .setParameter("b", book).getSingleResult();
        return c > 0;
    }

    @Override
    public long countBorrowsInPeriod(LocalDate start, LocalDate end) {
        Long c = em.createQuery("SELECT COUNT(r) FROM BorrowRecord r WHERE r.borrowDate >= :s AND r.borrowDate <= :e", Long.class)
                   .setParameter("s", start).setParameter("e", end).getSingleResult();
        return c != null ? c : 0;
    }

    @Override
    public long countReturnsInPeriod(LocalDate start, LocalDate end) {
        Long c = em.createQuery("SELECT COUNT(r) FROM BorrowRecord r WHERE r.returnDate >= :s AND r.returnDate <= :e", Long.class)
                   .setParameter("s", start).setParameter("e", end).getSingleResult();
        return c != null ? c : 0;
    }

    @Override
    public BigDecimal totalFinesCollected() {
        BigDecimal v = em.createQuery("SELECT COALESCE(SUM(r.fineAmount),0) FROM BorrowRecord r WHERE r.finePaid = true", BigDecimal.class).getSingleResult();
        return v != null ? v : BigDecimal.ZERO;
    }

    @Override
    public long countOverdue(LocalDate today) {
        Long c = em.createQuery("SELECT COUNT(r) FROM BorrowRecord r WHERE r.status='ACTIVE' AND r.dueDate < :t", Long.class)
                   .setParameter("t", today).getSingleResult();
        return c != null ? c : 0;
    }
}
