package com.tdc.nhom6.doanjava1.dao;
import com.tdc.nhom6.doanjava1.entity.Account;
import java.util.List;
public interface AccountDao {
    Account save(Account account);
    Account findById(Long id);
    Account findByUsername(String username);
    List<Account> findAll();
    void delete(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long count();
}
