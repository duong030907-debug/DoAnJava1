package com.tdc.nhom6.doanjava1.service;
import com.tdc.nhom6.doanjava1.entity.Account;
import java.util.List;
public interface AccountService {
    Account register(String username, String password, String fullName, String email);
    Account login(String username, String password);
    Account findById(Long id);
    List<Account> findAll();
    Account promote(Long id);
    Account demote(Long id);
    void delete(Long id);
    boolean existsByUsername(String username);
}
