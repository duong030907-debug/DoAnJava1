package com.tdc.nhom6.doanjava1.service;
import com.tdc.nhom6.doanjava1.entity.Book;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
public interface BookService {
    Book save(Book book);
    Book findById(Long id);
    List<Book> findAll();
    List<Book> findAvailable();
    List<Book> search(String keyword);
    List<Book> findByCategory(String category);
    Book update(Long id, Book updated);
    void delete(Long id);
    void uploadCover(Long bookId, MultipartFile file) throws IOException;
    boolean existsById(Long id);
}
