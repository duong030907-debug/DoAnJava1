package com.tdc.nhom6.doanjava1.dao.impl;
import com.tdc.nhom6.doanjava1.dao.AccountDao;
import com.tdc.nhom6.doanjava1.entity.Account;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AccountDaoImpl implements AccountDao {

    @PersistenceContext
    private EntityManager em;

    @Override public Account save(Account a)     { return em.merge(a); }
    @Override public Account findById(Long id)   { return em.find(Account.class, id); }

    @Override
    public Account findByUsername(String username) {
        try {
            return em.createQuery("SELECT a FROM Account a WHERE a.username = :u", Account.class)
                     .setParameter("u", username).getSingleResult();
        } catch (NoResultException e) { return null; }
    }

    @Override
    public List<Account> findAll() {
        return em.createQuery("SELECT a FROM Account a ORDER BY a.createdAt DESC", Account.class).getResultList();
    }

    @Override public void delete(Long id) { Account a = findById(id); if (a != null) em.remove(a); }

    @Override
    public boolean existsByUsername(String username) {
        Long count = em.createQuery("SELECT COUNT(a) FROM Account a WHERE a.username = :u", Long.class)
                       .setParameter("u", username).getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(a) FROM Account a WHERE LOWER(a.email) = LOWER(:e)", Long.class)
                       .setParameter("e", email).getSingleResult();
        return count > 0;
    }

    @Override
    public long count() {
        return em.createQuery("SELECT COUNT(a) FROM Account a", Long.class).getSingleResult();
    }
}
