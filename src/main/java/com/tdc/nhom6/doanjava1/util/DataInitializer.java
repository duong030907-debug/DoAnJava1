package com.tdc.nhom6.doanjava1.util;
import com.tdc.nhom6.doanjava1.dao.AccountDao;
import com.tdc.nhom6.doanjava1.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tạo tài khoản ADMIN mặc định khi khởi động lần đầu (chỉ khi DB trống).
 * Đăng nhập: username=admin / password=admin123
 */
@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired private AccountDao accountDao;
    private boolean initialized = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialized) return;
        initialized = true;
        if (accountDao.count() == 0) {
            Account admin = new Account();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtil.hash("admin123"));
            admin.setFullName("Quản Trị Viên");
            admin.setEmail("admin@library.edu.vn");
            admin.setRole("ADMIN");
            accountDao.save(admin);
            System.out.println("==============================================");
            System.out.println(" ADMIN mặc định đã được tạo:");
            System.out.println("   username : admin");
            System.out.println("   password : admin123");
            System.out.println("==============================================");
        }
    }
}
