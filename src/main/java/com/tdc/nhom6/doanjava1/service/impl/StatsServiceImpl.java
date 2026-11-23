package com.tdc.nhom6.doanjava1.service.impl;
import com.tdc.nhom6.doanjava1.dao.BookDao;
import com.tdc.nhom6.doanjava1.dao.BorrowRecordDao;
import com.tdc.nhom6.doanjava1.entity.Book;
import com.tdc.nhom6.doanjava1.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    @Autowired private BookDao bookDao;
    @Autowired private BorrowRecordDao borrowDao;

    @Override
    public List<Map<String,Object>> getMostBorrowed(int limit) {
        return bookDao.findMostBorrowed().stream().limit(limit > 0 ? limit : 10).map(row -> {
            Book b = (Book) row[0]; Long cnt = (Long) row[1];
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("bookId", b.getId()); m.put("title", b.getTitle());
            m.put("author", b.getAuthor()); m.put("borrowCount", cnt);
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String,Object> getInventory() {
        List<Book> books = bookDao.findAll();
        int total = books.stream().mapToInt(b -> b.getTotalCopies() != null ? b.getTotalCopies() : 0).sum();
        int avail = books.stream().mapToInt(b -> b.getAvailableCopies() != null ? b.getAvailableCopies() : 0).sum();
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("totalTitles", books.size()); m.put("totalCopies", total);
        m.put("availableCopies", avail); m.put("borrowedCopies", total - avail);
        return m;
    }

    @Override
    public Map<String,Object> getMonthlyActivity() {
        YearMonth now = YearMonth.now();
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("month", now.toString());
        m.put("totalBorrows", borrowDao.countBorrowsInPeriod(now.atDay(1), now.atEndOfMonth()));
        m.put("totalReturns", borrowDao.countReturnsInPeriod(now.atDay(1), now.atEndOfMonth()));
        m.put("currentOverdue", borrowDao.countOverdue(LocalDate.now()));
        return m;
    }

    @Override
    public Map<String,Object> getRevenue() {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("totalFinesCollected", borrowDao.totalFinesCollected());
        m.put("currency", "VND");
        return m;
    }

    @Override
    public long countOverdue() { return borrowDao.countOverdue(LocalDate.now()); }
}
