package com.tdc.nhom6.doanjava1.dao.impl;
import com.tdc.nhom6.doanjava1.dao.BookDao;
import com.tdc.nhom6.doanjava1.entity.Book;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BookDaoImpl implements BookDao {

    @PersistenceContext
    private EntityManager em;

    @Override public Book save(Book b)        { return em.merge(b); }
    @Override public Book findById(Long id)   { return em.find(Book.class, id); }
    @Override public boolean existsById(Long id) { return findById(id) != null; }

    @Override
    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class).getResultList();
    }

    @Override
    public List<Book> findAvailable() {
        return em.createQuery("SELECT b FROM Book b WHERE b.availableCopies > 0 ORDER BY b.title", Book.class).getResultList();
    }

    @Override
    public List<Book> searchByTitleOrAuthor(String kw) {
        String p = "%" + kw.toLowerCase() + "%";
        return em.createQuery(
            "SELECT b FROM Book b WHERE LOWER(b.title) LIKE :p OR LOWER(b.author) LIKE :p ORDER BY b.title", Book.class)
            .setParameter("p", p).getResultList();
    }

    @Override
    public List<Book> findByCategory(String cat) {
        return em.createQuery(
            "SELECT b FROM Book b WHERE LOWER(b.category) = LOWER(:c) ORDER BY b.title", Book.class)
            .setParameter("c", cat).getResultList();
    }

    @Override
    public void delete(Book b) {
        Book managed = em.contains(b) ? b : em.merge(b);
        em.remove(managed);
    }

    @Override
    public List<Object[]> findMostBorrowed() {
        return em.createQuery(
            "SELECT b, COUNT(br) AS cnt FROM BorrowRecord br JOIN br.book b GROUP BY b ORDER BY cnt DESC",
            Object[].class).getResultList();
    }
}
