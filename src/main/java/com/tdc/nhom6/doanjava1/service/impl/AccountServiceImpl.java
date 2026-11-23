package com.tdc.nhom6.doanjava1.service.impl;
import com.tdc.nhom6.doanjava1.dao.AccountDao;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.service.AccountService;
import com.tdc.nhom6.doanjava1.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired private AccountDao accountDao;

    @Override
    public Account register(String username, String password, String fullName, String email) {
        if (username == null || username.trim().length() < 3 || username.trim().length() > 50)
            throw new RuntimeException("Tên đăng nhập phải từ 3 đến 50 ký tự");
        if (password == null || password.length() < 6)
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        if (fullName == null || fullName.trim().isEmpty())
            throw new RuntimeException("Họ tên không được để trống");
        username = username.trim();
        fullName = fullName.trim();
        String normalizedEmail = (email != null && !email.trim().isEmpty()) ? email.trim() : null;
        if (accountDao.existsByUsername(username))
            throw new RuntimeException("Tên đăng nhập đã tồn tại: " + username);
        if (normalizedEmail != null && accountDao.existsByEmail(normalizedEmail))
            throw new RuntimeException("Email đã được sử dụng: " + normalizedEmail);
        Account a = new Account();
        a.setUsername(username);
        a.setPassword(PasswordUtil.hash(password));
        a.setFullName(fullName);
        a.setEmail(normalizedEmail);
        a.setRole("STUDENT");
        return accountDao.save(a);
    }

    @Override
    public Account login(String username, String password) {
        Account a = accountDao.findByUsername(username);
        if (a == null || !PasswordUtil.matches(password, a.getPassword()))
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng");
        if (!a.isActive())
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        return a;
    }

    @Override public Account findById(Long id)   { return accountDao.findById(id); }
    @Override public List<Account> findAll()     { return accountDao.findAll(); }
    @Override public boolean existsByUsername(String u) { return accountDao.existsByUsername(u); }

    @Override
    public Account promote(Long id) {
        Account a = accountDao.findById(id);
        if (a == null) throw new RuntimeException("Không tìm thấy tài khoản");
        if (a.isAdmin()) throw new RuntimeException("Không thể thay đổi quyền của tài khoản ADMIN");
        a.setRole("LIBRARIAN");
        return accountDao.save(a);
    }

    @Override
    public Account demote(Long id) {
        Account a = accountDao.findById(id);
        if (a == null) throw new RuntimeException("Không tìm thấy tài khoản");
        if (a.isAdmin()) throw new RuntimeException("Không thể hạ cấp tài khoản ADMIN");
        a.setRole("STUDENT");
        return accountDao.save(a);
    }

    @Override
    public void delete(Long id) {
        Account a = accountDao.findById(id);
        if (a == null) throw new RuntimeException("Không tìm thấy tài khoản");
        if (a.isAdmin()) throw new RuntimeException("Không thể vô hiệu hóa tài khoản ADMIN");
        a.setActive(false);
        accountDao.save(a);
    }
}
