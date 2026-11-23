package com.tdc.nhom6.doanjava1.dao;
import com.tdc.nhom6.doanjava1.entity.Book;
import java.util.List;
public interface BookDao {
    Book save(Book book);
    Book findById(Long id);
    List<Book> findAll();
    List<Book> findAvailable();
    List<Book> searchByTitleOrAuthor(String keyword);
    List<Book> findByCategory(String category);
    void delete(Book book);
    boolean existsById(Long id);
    List<Object[]> findMostBorrowed();
}
