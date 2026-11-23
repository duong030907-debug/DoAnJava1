package com.tdc.nhom6.doanjava1.service.impl;
import com.tdc.nhom6.doanjava1.dao.BookDao;
import com.tdc.nhom6.doanjava1.dao.BorrowRecordDao;
import com.tdc.nhom6.doanjava1.entity.Book;
import com.tdc.nhom6.doanjava1.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired private BookDao bookDao;
    @Autowired private BorrowRecordDao borrowDao;

    @Override
    public Book save(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        return bookDao.save(book);
    }

    @Override public Book findById(Long id)           { return bookDao.findById(id); }
    @Override public List<Book> findAll()             { return bookDao.findAll(); }
    @Override public List<Book> findAvailable()       { return bookDao.findAvailable(); }
    @Override public List<Book> search(String kw)     { return bookDao.searchByTitleOrAuthor(kw); }
    @Override public List<Book> findByCategory(String c) { return bookDao.findByCategory(c); }
    @Override public boolean existsById(Long id)      { return bookDao.existsById(id); }

    @Override
    public Book update(Long id, Book updated) {
        Book b = bookDao.findById(id);
        if (b == null) throw new RuntimeException("Không tìm thấy sách");
        if (updated.getTitle()       != null) b.setTitle(updated.getTitle());
        if (updated.getAuthor()      != null) b.setAuthor(updated.getAuthor());
        if (updated.getCategory()    != null) b.setCategory(updated.getCategory());
        if (updated.getPublisher()   != null) b.setPublisher(updated.getPublisher());
        if (updated.getPublishYear() != null) b.setPublishYear(updated.getPublishYear());
        if (updated.getDescription() != null) b.setDescription(updated.getDescription());
        if (updated.getTotalCopies() != null && updated.getTotalCopies() > 0) {
            int borrowed = b.getTotalCopies() - b.getAvailableCopies();
            if (updated.getTotalCopies() < borrowed) {
                throw new RuntimeException("Số lượng bản không thể nhỏ hơn số bản đang được mượn (" + borrowed + ")");
            }
            b.setTotalCopies(updated.getTotalCopies());
            b.setAvailableCopies(updated.getTotalCopies() - borrowed);
        }
        return bookDao.save(b);
    }

    @Override
    public void delete(Long id) {
        Book b = bookDao.findById(id);
        if (b == null) throw new RuntimeException("Không tìm thấy sách");
        if (borrowDao.existsByBook(b)) {
            throw new RuntimeException("Không thể xóa sách đã có lịch sử mượn trả");
        }
        bookDao.delete(b);
    }

    @Override
    public void uploadCover(Long bookId, MultipartFile file) throws IOException {
        Book b = bookDao.findById(bookId);
        if (b == null) throw new RuntimeException("Không tìm thấy sách");
        b.setCoverImage(file.getBytes());
        b.setCoverType(file.getContentType());
        bookDao.save(b);
    }
}
