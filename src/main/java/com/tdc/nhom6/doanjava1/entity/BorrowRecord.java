package com.tdc.nhom6.doanjava1.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDate borrowDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, RETURNED, RETURNED_LATE

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    private boolean finePaid = false;

    @Column(length = 500)
    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public BorrowRecord() {}

    // ── Computed ───────────────────────────────────────────────────────
    public boolean isOverdue() {
        return "ACTIVE".equals(status) && LocalDate.now().isAfter(dueDate);
    }

    // ── Getters & Setters ─────────────────────────────────────────────
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }
    public Account getAccount()                  { return account; }
    public void setAccount(Account a)            { this.account = a; }
    public Book getBook()                        { return book; }
    public void setBook(Book b)                  { this.book = b; }
    public LocalDate getBorrowDate()             { return borrowDate; }
    public void setBorrowDate(LocalDate d)       { this.borrowDate = d; }
    public LocalDate getDueDate()                { return dueDate; }
    public void setDueDate(LocalDate d)          { this.dueDate = d; }
    public LocalDate getReturnDate()             { return returnDate; }
    public void setReturnDate(LocalDate d)       { this.returnDate = d; }
    public String getStatus()                    { return status; }
    public void setStatus(String s)              { this.status = s; }
    public BigDecimal getFineAmount()            { return fineAmount; }
    public void setFineAmount(BigDecimal f)      { this.fineAmount = f; }
    public boolean isFinePaid()                  { return finePaid; }
    public void setFinePaid(boolean f)           { this.finePaid = f; }
    public String getNotes()                     { return notes; }
    public void setNotes(String n)               { this.notes = n; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime c)    { this.createdAt = c; }
}
